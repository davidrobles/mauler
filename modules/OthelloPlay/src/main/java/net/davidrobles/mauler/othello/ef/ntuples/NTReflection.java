package net.davidrobles.mauler.othello.ef.ntuples;

public enum NTReflection
{
    VERTICAL {
        @Override
        int reflect(int index) {
            int div = index / 8;
            int mod = index % 8;
            return (63 - 7 - (div * 8) + mod);
        }
    }, HORIZONTAL {
        @Override
        int reflect(int index) {
            int div = index / 8;
            int mod = index % 8;
            return (div * 8) + 7 - mod;
        }
    };

    abstract int reflect(int index);
}
