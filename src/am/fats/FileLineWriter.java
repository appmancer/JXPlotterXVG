package am.fats;

import java.io.FileWriter;
import java.io.IOException;

public class FileLineWriter extends FileWriter{
    public FileLineWriter(String filename) throws IOException {
        super(filename, false);
    }

    public void writeLine(String line) throws IOException {
        super.write(line);
        super.write(System.lineSeparator());
    }
}
