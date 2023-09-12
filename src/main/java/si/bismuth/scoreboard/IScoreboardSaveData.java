package si.bismuth.scoreboard;

import net.minecraft.nbt.NbtList;

public interface IScoreboardSaveData {
    void readScores(NbtList p_readScores_1_);

    NbtList scoresToNbt();
}
