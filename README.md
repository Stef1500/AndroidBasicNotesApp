# AndroidBasicNotesApp

Semester Project: Design Document

Stefano Buglione

12/07/2024

Prof. Heidi Gentry

Purpose of the application: NotesApp+ is an application aimed at expanding the idea behind
lab #4 where we were first introduced to Datastore, which was my favorite concept this semester.
The idea of this app is not to make a better product than other note apps already in the market,
but to lay a foundation to possibly add more features in future to make a better notes app.
Features implemented:

-Full UI built using jetpack compose

-A ViewModel that handles configuration/orientation changes to not lose any data

-Notes independent from one another. i.e. each note created is its own entity, deleting that
note will delete all content inside it.

-Several dialogs for user interaction when changing app configurations and adding new
notes

-A persistent storage for all new data using DataStore (makes sure all images, notes, and
configurations are persistent even after closing the app)

-Added feature where the camera can be used to take photos and places those photos in the
notes (with permission handling)


Challenges:
-Throughout the development of this application, there were many problems when testing
the ViewModel for each version of the app, many updates to the ViewModel files were
necessary each time to make sure it handles configuration changes properly

-Implementing the camera feature was by far the hardest part in the development of this
application, at first there were many aspects that didn’t work properly, including
permission handling, how to make the image taken persist to only one note, making the
image fit properly into the note editor. Moreover, the current version of the application
does not fix all the bugs related to the camera, there are still bugs of photos persisting
even when they should be deleted and more.
