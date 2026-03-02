package net.davidrobles.mauler.core;

public interface InstantiablePlayer<GAME extends Game<GAME>>
{
    GAME newInstance();
}
