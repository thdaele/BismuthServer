package si.bismuth.utils;

public interface IRecipeBookItemDuper {
	void clearDupeItem();

	void dupeItem(int slot);

	int getDupeItem();

	void dupeItemScan(boolean s);
}
