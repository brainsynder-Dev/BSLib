package lib.brainsynder.files;

import lib.brainsynder.nbt.CompressedStreamTools;
import lib.brainsynder.nbt.StorageTagCompound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageFile extends StorageTagCompound {
    private File file;

    public StorageFile(File file) {
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }

        try { // Will fix the data not being read from the file
            FileInputStream stream = new FileInputStream(file);
            StorageTagCompound compound = CompressedStreamTools.readCompressed(stream);
            compound.getKeySet().forEach(key -> {
                setTag(key, compound.getTag(key));
            });
            stream.close();
        } catch (IOException ignored) {}

        this.file = file;
    }

    public StorageFile setDefault (StorageTagCompound compound) {
        compound.getKeySet().forEach(key -> {
            setTag(key, compound.getTag(key));
        });
        return this;
    }

    public void save () {
        try {
            if (!file.exists()) file.createNewFile();
            FileOutputStream stream = new FileOutputStream(file);
            CompressedStreamTools.writeCompressed(this, stream);
            stream.close();
        }catch (Exception ignored){}
    }
}
