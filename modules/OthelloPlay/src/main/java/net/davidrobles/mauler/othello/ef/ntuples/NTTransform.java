package net.davidrobles.mauler.othello.ef.ntuples;

public enum NTTransform
{
    REF_HOR {
        @Override
        int transform(int index) {
            return NTReflection.HORIZONTAL.reflect(index);
        }
    }, REF_VER {
        @Override
        int transform(int index) {
            return NTReflection.VERTICAL.reflect(index);
        }
    }, REF_HOR_VER {
        @Override
        int transform(int index) {
            return NTReflection.VERTICAL.reflect(NTReflection.HORIZONTAL.reflect(index));
        }
    }, ROT {
        @Override
        int transform(int index) {
            int div = index / 8;
            int mod = index % 8;
            return 63 - (mod * 8) - div;
        }
    }, ROT_REF_HOR {
        @Override
        int transform(int index) {
            return NTReflection.HORIZONTAL.reflect(ROT.transform(index));
        }
    }, ROT_REF_VER {
        @Override
        int transform(int index) {
            return NTReflection.VERTICAL.reflect(ROT.transform(index));
        }
    }, ROT_REF_HOR_VER {
        @Override
        int transform(int index) {
            return NTReflection.HORIZONTAL.reflect(NTReflection.VERTICAL.reflect(ROT.transform(index)));
        }
    };

    abstract int transform(int index);
}
