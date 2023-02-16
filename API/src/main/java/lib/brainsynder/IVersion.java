package lib.brainsynder;

import lib.brainsynder.utils.Triple;

public interface IVersion {
    String name ();

    String getNMS ();

    Triple<Integer, Integer, Integer> getVersionParts();

    IVersion getParent ();
}