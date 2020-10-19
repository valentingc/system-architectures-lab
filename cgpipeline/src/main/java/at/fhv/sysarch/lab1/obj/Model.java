package at.fhv.sysarch.lab1.obj;

import java.util.Collections;
import java.util.List;

public class Model {
    private List<Face> faces;

    public Model(List<Face> faces) {
        this.faces = faces;
    }

    public List<Face> getFaces() {
        return Collections.unmodifiableList(this.faces);
    }
}