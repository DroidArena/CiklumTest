package com.globekeeper.uploader

import androidx.work.WorkManager
import com.globekeeper.uploader.data.FileRepository
import com.globekeeper.uploader.data.database.AppDatabase
import com.globekeeper.uploader.data.database.dao.UploadDao
import com.globekeeper.uploader.data.models.FileInfo
import com.globekeeper.uploader.domain.UploadInteractorImpl
import com.globekeeper.uploader.errors.UploadMaxSizeException
import com.globekeeper.uploader.errors.UploadsEmptyException
import com.globekeeper.uploader.errors.UploadsMaxCountException
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(MockitoJUnitRunner::class)
class ValidationTest {
    companion object {
        private const val MAX_FILES_COUNT = 2
        private const val MAX_SIZE = 10L
    }
    @Mock
    private lateinit var mockWorkManager: WorkManager

    @Mock
    private lateinit var uploadDao: UploadDao

    @Mock
    private lateinit var mockAppDatabase: AppDatabase

    @Mock
    private lateinit var mockFileRepository: FileRepository

    private lateinit var uploadInteractor: UploadInteractorImpl

    @Before
    fun setup() {
        uploadInteractor = UploadInteractorImpl(mockWorkManager, mockFileRepository, mockAppDatabase, MAX_FILES_COUNT, MAX_SIZE)
        `when`(mockAppDatabase.uploadDao()).thenReturn(uploadDao)
    }

    @Test
    fun emptyFilesListTest() = runBlocking {
        val uris = emptyList<String>()
        try {
            uploadInteractor.scheduleUploadsByUris(uris)

            Unit
        } catch (e: Exception) {
            assertTrue("UploadsEmptyException is expected but get ${e.javaClass.name}", e is UploadsEmptyException)
        }
    }

    @Test
    fun maxFileSizeTest() = runBlocking {
        val uris = listOf(
            "content://images/1.jpg", "content://images/2.jpg")

        `when`(mockFileRepository.getFilesInfo(uris))
            .thenReturn(listOf(
                FileInfo(uris[0], "1.jpg", mb(5L)),
                FileInfo(uris[1], "2.jpg", mb(MAX_SIZE + 1))
            ))
        try {
            uploadInteractor.scheduleUploadsByUris(uris)

            Unit
        } catch (e: Exception) {
            assertTrue("UploadMaxSizeException is expected but get ${e.javaClass.name}", e is UploadMaxSizeException)
            assertEquals((e as UploadMaxSizeException).maxSize, MAX_SIZE)
        }
    }

    @Test
    fun maxFileCountTest() = runBlocking {
        val uris = listOf(
            "content://images/1.jpg", "content://images/2.jpg", "content://images/3.jpg")

        try {
            uploadInteractor.scheduleUploadsByUris(uris)

            Unit
        } catch (e: Exception) {
            assertTrue("UploadsMaxCountException is expected but get ${e.javaClass.name}", e is UploadsMaxCountException)
            assertEquals((e as UploadsMaxCountException).count, MAX_FILES_COUNT)
        }
    }

    @Test
    fun validTest() = runBlocking {
        val uris = listOf(
            "content://images/1.jpg", "content://images/2.jpg")

        `when`(mockFileRepository.getFilesInfo(uris))
            .thenReturn(uris.map{
                FileInfo(it, it.substringAfterLast('.'), mb(MAX_SIZE))
            })
        uploadInteractor.scheduleUploadsByUris(uris)

        Unit
    }

    @After
    fun tearDown() {
    }

    private fun mb(size: Long) = size * 1024 * 1024
}
