package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.othello.ef.ntuples.NTUtil;
import net.davidrobles.mauler.othello.ef.ntuples.NTupleSystem;
import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCUtil;

public class OthelloVF
{
    // Value functions
    public static WPC             WPC_SYM = new WPC(WPCUtil.load("dr-sym-6462"));
    public static NTupleSystem NTS_RND = NTUtil.load("turing-5000-0.228");
    public static NTupleSystem     NTS_RS = NTUtil.load("best-1");
    public static NTupleSystem    NTS_LOG = NTUtil.load("logistello11-130000-0.822");
    public static NTupleSystem    NTS_EVO = NTUtil.load("buenero-56");
}
