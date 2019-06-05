package com.builtbroken.elevators.logic;

import com.google.gson.JsonObject;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.function.BooleanSupplier;

/**
 * Used to check if an item is loaded for a recipe to be loaded as well
 * Created by Dark(DarkGuardsman, Robert) on 6/5/2019.
 */
public class ConditionItemExistsValue implements IConditionFactory
{
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json)
    {
        final boolean condition = Boolean.parseBoolean(JsonUtils.getString(json, "condition").toLowerCase());
        final String value = JsonUtils.getString(json, "value");
        return () -> checkExists(value.trim()) == condition;
    }

    private boolean checkExists(String value)
    {
        if(value.toLowerCase().startsWith("ore@"))
        {
            final NonNullList<ItemStack> list = OreDictionary.getOres(value.substring(4));
            for (ItemStack stack : list)
            {
                if (!stack.isEmpty())
                {
                    return true;
                }
            }
        }
        else
        {
            final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(value));
            if(item != null && item != Items.AIR)
            {
                return true;
            }
        }
        return false;
    }
}
