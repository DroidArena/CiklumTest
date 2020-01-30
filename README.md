# GlobeKeeperUploader

* Tested on Android tablet (6.0), phone (9.0)

# Notices
- files multi selection is done by long tapping on images in file chooser

# Extra features:
- uploads persist over app restart (but not upload progress)
- while offline, uploads will wait until internet is connected

# Known limitations:
- all uploads run in parallel, there is no limit for concurrent jobs 
- files from file chooser can be selected only once and then uploader starts, file chooser will be available again only when you clear all you upload jobs. File sizes and files count validation checks all selected and denies all or accepts all

# Known issues:
- upload progress doesn't completely work as expected and investigation exceeds test task's time 
