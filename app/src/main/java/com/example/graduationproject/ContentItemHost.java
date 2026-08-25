package com.example.graduationproject;

import com.example.graduationproject.models.ContentItem;

/**
 * Contract that host Activities (which display video / podcast content
 * in a list and can navigate to the player screen) must implement.
 *
 * <p>Used by {@link com.example.graduationproject.ui.VideoLibraryFragment}
 * and {@link com.example.graduationproject.ui.PlayerFragment} so that
 * navigation logic does not depend on a concrete Activity class.</p>
 */
public interface ContentItemHost {
    /** Open the full-screen player for the given content item. */
    void openPlayer(ContentItem item);
}