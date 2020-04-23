package lib.brainsynder.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListPager<T> extends ArrayList<T> {
    private final int contentLimit;

    /**
     * @param contentLimit - Number of items can be in each page
     */
    public ListPager(int contentLimit) {
        this(contentLimit, new ArrayList<>());
    }

    /**
     * @param contentLimit - Number of items can be in each page
     * @param objects - Collection of items to be added to the list
     */
    public ListPager(int contentLimit, T... objects) {
        this(contentLimit, Arrays.asList(objects));
    }

    /**
     * @param contentLimit - Number of items can be in each page
     * @param objects - Collection of items to be added to the list
     */
    public ListPager(int contentLimit, List<T> objects) {
        this.contentLimit = contentLimit;
        addAll(objects);
    }

    /**
     * Will return how many items can be in each page
     */
    public int getContentLimit() {
        return contentLimit;
    }

    /**
     * Will get the total number of pages in the list
     */
    public int totalPages() {
        return (((int) Math.ceil((double) size() / contentLimit)));
    }

    /**
     * Will check if there is a selected page
     *
     * @param page - Page number to check
     */
    public boolean exists(int page) {
        page = (page - 1);
        return !(page < 0) && page < totalPages();
    }

    /**
     * Will fetch the items for the selected page
     *
     * @param page - Selected page
     * @return {@link java.util.List}
     */
    public List<T> getPage(int page) {
        page = (page - 1);
        if (page < 0 || page >= totalPages()) throw new IndexOutOfBoundsException("Index: " + page + ", Size: " + totalPages());
        List<T> objects = new ArrayList<>();
        int min = page * contentLimit;
        int max = ((page * contentLimit) + contentLimit);
        if (max > size()) max = size();
        for (int i = min; max > i; i++) objects.add(get(i));
        return objects;
    }
}
