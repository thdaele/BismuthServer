package carpet.bismuth;

import carpet.bismuth.logging.LoggerRegistry;
import carpet.bismuth.utils.HUDController;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;

public class CarpetServer // static for now - easier to handle all around the code, its one anyways
{
	public static MinecraftServer minecraft_server;

	private static final IRecipe duration1 = new ShapelessRecipes("rocket", makeFirework(1), NonNullList.from(Ingredient.EMPTY, Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.GUNPOWDER)));
	private static final IRecipe duration2 = new ShapelessRecipes("rocket", makeFirework(2), NonNullList.from(Ingredient.EMPTY, Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.GUNPOWDER), Ingredient.fromItems(Items.GUNPOWDER)));
	private static final IRecipe duration3 = new ShapelessRecipes("rocket", makeFirework(3), NonNullList.from(Ingredient.EMPTY, Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.GUNPOWDER), Ingredient.fromItems(Items.GUNPOWDER), Ingredient.fromItems(Items.GUNPOWDER)));

	static {
		CraftingManager.register("carpet:durationone", duration1);
		CraftingManager.register("carpet:durationtwo", duration2);
		CraftingManager.register("carpet:durationthree", duration3);
	}

	private static ItemStack makeFirework(int duration) {
		final NBTTagCompound durationTag = new NBTTagCompound();
		final NBTTagCompound fireworksTag = new NBTTagCompound();
		durationTag.setByte("Flight", (byte) duration);
		fireworksTag.setTag("Fireworks", durationTag);
		final ItemStack firework = new ItemStack(Items.FIREWORKS, 3);
		firework.setTagCompound(fireworksTag);
		return firework;
	}

	public static void init(MinecraftServer server) //aka constructor of this static singleton class
	{
		minecraft_server = server;
	}

	public static void onServerLoaded(MinecraftServer server) {
		LoggerRegistry.initLoggers(server);
	}

	public static void tick(MinecraftServer server) {
		HUDController.update_hud(server);
	}

	public static void playerConnected(EntityPlayerMP player) {
		LoggerRegistry.playerConnected(player);
		unlockCustomRecipes(player);
	}

	public static void playerDisconnected(EntityPlayerMP player) {
		LoggerRegistry.playerDisconnected(player);
	}

	private static void unlockCustomRecipes(EntityPlayerMP player) {
		player.unlockRecipes(Lists.newArrayList(duration1, duration2, duration3));
	}
}
