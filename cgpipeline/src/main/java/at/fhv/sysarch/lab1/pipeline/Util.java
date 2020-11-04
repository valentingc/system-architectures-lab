package at.fhv.sysarch.lab1.pipeline;

import at.fhv.sysarch.lab1.obj.Face;
import com.hackoeur.jglm.Vec4;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class Util {

    /**
     * Determines whether a face is the special marker face indicating the end of the data stream.
     *
     * @param face The possible marker face
     *
     * @return true if face marks end, false otherwise
     */
    public static boolean isFaceMakingEnd(Face face) {
        if (null == face) {
            return false;
        }

        return face.getV1().equals(Vec4.VEC4_ZERO) &&
                face.getV2().equals(Vec4.VEC4_ZERO) &&
                face.getV3().equals(Vec4.VEC4_ZERO) &&
                face.getN1().equals(Vec4.VEC4_ZERO) &&
                face.getN2().equals(Vec4.VEC4_ZERO) &&
                face.getN3().equals(Vec4.VEC4_ZERO);
    }
}
