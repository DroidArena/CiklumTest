# GlobeKeeperUploader

Extra features:
- uploads persist over app restart
- while offline, uploads will wait until internet is connected

Known limitations:
- all uploads run in parallel, there is no limit for concurrent jobs 
- files from file chooser can be selected only once and then uploader starts, file chooser will be available again only when you clear all you upload jobs

Known issues:
- upload progress doesn't completely work as expected and investigation exceeds test task's time 
