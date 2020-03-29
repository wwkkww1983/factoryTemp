package com.proton.carepatchtemp.view.mutablepageradapter;

import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Common interface of MutablePagerAdapter(s).
 */
public interface MutablePageControl {

    /**
     * Get page fragment at specific index,
     *
     * @return Page fragment
     */
    Fragment getPageFragment(int index);

    /**
     * Search position of specific page fragment.
     *
     * @return Position of fragment
     */
    int indexOfPageFragment(Fragment fragment);

    /**
     * Add page fragment to last.
     *
     * @param fragment Page fragment to add
     */
    void addPageFragment(Fragment fragment);

    /**
     * Add all elements from page fragment list to last.
     *
     * @param fragments Page fragment list to add
     */
    void addPageFragments(List<Fragment> fragments);

    /**
     * Insert page fragment into specific index.
     *
     * @param fragment Page fragment to insert
     * @param index    Insert index
     */
    void insertPageFragment(Fragment fragment, int index);

    /**
     * Replace page fragment at specific index to new.
     *
     * @param fragment New page fragment
     * @param index    Replace index
     */
    void replacePageFragment(Fragment fragment, int index);

    /**
     * Remove last page fragment,
     */
    void removeLastPageFragment();

    /**
     * Remove page fragment at specific index.
     *
     * @param index Remove index
     */
    void removePageFragment(int index);

    /**
     * Remove specific page fragment.
     *
     * @param fragment Page fragment to remove
     */
    void removePageFragment(Fragment fragment);

    /**
     * Remove all page fragment.
     */
    void clearAllPageFragments();
}
