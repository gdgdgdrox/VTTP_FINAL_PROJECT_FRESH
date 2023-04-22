TO DO LIST

-Refactor System.out.println to logger.info so to track if app crashes
-Log daily scheduler result (e.g. 19/4/2023 08:00 - Calling API to check for new deals.. no deals found || 2 new deals found, adding to db)
-Some result e.g. tours does not have coordinates (0 provided). if coordinates not available, inform user instead 
-use AWS s3 instead of Digital Ocean
- If server threw exception while we trying to download image, how to handle?


less important
-check what is this "JAXB is unavailable. Will fallback to SDK implementation which may be less performant.If you are using Java 9+, you will need to include javax.xml.bind:jaxb-api as a dependency." when saving image to Digital Ocean