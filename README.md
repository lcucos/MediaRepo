MediaRepo
=========

Local/centralized repository to store and track media files (image, audio, video). 


What is this about? I have a few tens of thousands of images and videos stored on a home server, 
I also have hundrends of videos and images (that pop on my laptops, SD cards, tablets, phones) every month.
Two problems I need to address:
1. Sometime I lose track of what I uploaded/organized on the home server
2. I need to backup my home server somewhere cheap (at the moment, flicker seems to be the most cost effective store). 

Here is what I am looking for:
1. Index / reindex media files as needed (without being forced into a fix file format)
2. Have a flexible client / server architecture
3. Scan local folders and look for potential duplicates on the centralized repository
4. Hook into any online store (flickr, AWS-S3, dropbox, etc), and upload, show status etc
5. Potentially migrate data between online repositories as needed
6. Easy data import from any source
7. Backup metadata somewhere else online
8. Track deletes/modifications

What I don't want: To be locked in a proprietary solution (ex: dropbox, amazon, google, etc)

Current status: very basic functionality (the result of a few hours in a weekend)
- client/server architecture
- flexible storage infrastructure (currently only in-memory and remote store is implemented) mogodb will be next
- scans local folders and query the centralized repo for potential matches

Require: java 1.6 or above


