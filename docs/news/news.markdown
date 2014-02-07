#News

##Screens

###Top List

News root view. Top three stories from each category. No dropdown for switching categories.

<a href="top_list_01.png"><img class="screen" src="top_list_01.png" width="160"></a>

Allowed Orientations: Portrait only

__Action Bar__

Title = MIT News

Search button on right. See [Search](#search).

__Content__

_pull-to-refresh Tableview_

+ Sections

    - One for each category in [http://m.mit.edu/apis/news/categories](http://m.mit.edu/apis/news/categories) (i.e. Latest MIT News, In the Media, and Around Campus) in the order provided by the server.

    - Last row in each section is titled "More x...", e.g. "More Latest MIT News...". Tap that section header to browse all stories in that category (leads to [Category List](#categorylist)).

+ Rows

    - Top List screen shows top 3 stories in each category.
    
    - Stories in Latest MIT News (type == `mit_news`) and Around Campus (type == `around_campus`) use the same layout.
    
        + Minimum row height of 85pt.
        + Image scaled and cropped to be 86pt x 61pt.
        + Story titles 17pt bold Roboto.
        + Story dek 14pt regular Roboto.

    - Stories in In the Media (type == `in_the_media`) use a different layout.

        + Minimum row height of 85pt.
        + Image placed in bottom right.
        + No title
        + Story dek 14pt regular Roboto.

    - Story images should be loaded asynchronously.

+ Interaction

    - Search button leads to [Search](#search) mode.
    - Tapping a story that isn't from "In the Media" pushes in [Story Detail](#storydetail) screen.
    - Tapping an In the Media story opens the URL for that story in the device's default browser.

###Category List

Same as Top List, but with one dropdown menu at the top for switching between categories and an extra row at the bottom to load more stories, similar to how News works and appears in MIT Mobile 2.4.

<a href="category_list_01.png"><img class="screen" src="category_list_01.png" width="160"></a> <a href="category_list_02.png"><img class="screen" src="category_list_02.png" width="160"></a> <a href="category_list_03.png"><img class="screen" src="category_list_03.png" width="160"></a>

Allowed Orientations: Portrait only

__Action Bar__

Underlined "MIT News" title. Tapping it goes back to Top List.

Category title doubles as a dropdown menu for switching to another category.

Stories are loaded 20 at a time. More can be loaded from the "Load more..." row at the bottom of the table. Pull-to-refresh to reload the first 20.

When tapped, "Load More…" turns into "Loading…" and gets a disabled grey color until loading completes or fails.

Search button leads to [Search](#search) mode.

Category List fetches stories using URLs like [http://m.mit.edu/apis/news/stories?category=mit_news&limit=20](http://m.mit.edu/apis/news/stories?category=mit_news&limit=20).

###Search

Modal state of [Top List](#toplist) and [Category List](#categorylist) screens. Same behavior as in MIT Mobile 2.4.

Searching from Top List and Category List both search across all story types.

Search results are loaded 20 at a time. More can be loaded from the "Load more..." row at the bottom of the table, just as in Category List.

Search uses URLs like [http://m.mit.edu/apis/news/stories?q=banana&limit=20](http://m.mit.edu/apis/news/stories?q=banana&limit=20).

###Story Detail

<a href="detail_01.png"><img class="screen" src="detail_01.png" width="160"></a> <a href="detail_02.png"><img class="screen" src="detail_02.png" width="160"></a> 

Allowed Orientations: Portrait only

__Action Bar__

Title = blank

"Share" button in overflow menu on action bar's right. Shares the story's `website_url`. Includes `title` and `dek` as appropriate. No longer a button in the story itself.

No bookmarking of stories for now.

Content is a web view template. Story's primary image, if available, is placed at top. Image can be tapped to go to [Image Gallery](#imagegallery). If story has images but no primary image, the gallery can be reached via an image-less link to the gallery below the story title/dek/byline area.

Updated web template forthcoming.

###Image Gallery

Same as 2.4 behavior for now.

##Shared Elements and Special Behaviors

###Caching, Loading, and Refreshing

Only the [Top List](#toplist) and [Category List](#categorylist) screens cache stories and refresh their contents when they become visible if their data is stale. Searches aren't cached, and the Story Detail screen just takes what its parent gives it.

The two API resources to be cached are the category list call ([http://m.mit.edu/apis/news/categories](http://m.mit.edu/apis/news/categories)) and the list of stories in those categories (e.g. [http://m.mit.edu/apis/news/stories?category=mit_news](http://m.mit.edu/apis/news/stories?category=mit_news)).

Both of those include cache-control headers which should be relied upon to decide how long to cache that data. Top List and Category List should check, when they become visible, if their relevant cached data is stale and refresh it as needed.

Story images should be loaded lazily.

###Loading More

The new APIs on m.mit.edu include pagination links in their response headers in the form of a Link header. This can be used when "Load More…" is tapped, rather than constructing URLs and trying to figure out when the end of a list has been reached. [See the docs for more info](http://mobile-dev.mit.edu/docs/overview.html#pagination).