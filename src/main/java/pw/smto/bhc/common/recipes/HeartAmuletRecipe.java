package pw.smto.bhc.common.recipes;

import com.google.gson.JsonObject;
import com.google.gson.internal.NonNullElementWrapperList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.SerializationContext;
import io.wispforest.owo.util.RegistryAccess;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import pw.smto.bhc.common.BaubleyHeartCanisters;
import pw.smto.bhc.common.Registry;
import pw.smto.bhc.common.util.InventoryUtil;

public class HeartAmuletRecipe extends ShapelessRecipe {

    final String group;
    final ItemStack result;

    final CraftingRecipeCategory category = CraftingRecipeCategory.EQUIPMENT;
    final DefaultedList<Ingredient> ingredients;

    public HeartAmuletRecipe(String group, CraftingRecipeCategory category, ItemStack stack, DefaultedList<Ingredient> list) {
        // new Identifier(BaubleyHeartCanisters.MOD_ID, "amulet_shapeless"),
        super(group, CraftingRecipeCategory.EQUIPMENT, stack, list);
        this.group = group;
        this.result = stack;
        this.ingredients = list;
    }

    @Override
    public ItemStack craft(RecipeInputInventory recipeInputInventory, RegistryWrapper.WrapperLookup wrapperLookup) {
        ItemStack oldCanister = ItemStack.EMPTY;
        for (int i = 0; i < recipeInputInventory.size(); i++) {
            ItemStack input = recipeInputInventory.getStack(i);
            if(input.getItem() == Registry.Items.HEART_AMULET) {
                oldCanister = input;
                break;
            }
        }
        ItemStack stack = super.craft(recipeInputInventory, wrapperLookup);
        SimpleInventory oldInv = InventoryUtil.createVirtualInventory(4, oldCanister);
        SimpleInventory newInv = InventoryUtil.createVirtualInventory(5, stack);
        for (int i = 0; i < oldInv.size(); i++) {
            newInv.setStack(i, oldInv.getStack(i));
        }
        InventoryUtil.serializeInventory(newInv, stack);
        return stack;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registry.RecipeSerializers.AMULET_SHAPELESS;
    }

    public static class BHCSerializer implements RecipeSerializer<HeartAmuletRecipe> {

        public static final PacketCodec<RegistryByteBuf, HeartAmuletRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                BHCSerializer::write, BHCSerializer::read
        );

        public static HeartAmuletRecipe read(PacketByteBuf pBuffer) {
            String s = pBuffer.readString();
            int i = pBuffer.readVarInt();
            DefaultedList<Ingredient> nonnulllist = DefaultedList.ofSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, pBuffer.read(Endec.ofCodec(Ingredient.DISALLOW_EMPTY_CODEC)));
            }

            ItemStack itemstack = pBuffer.read(Endec.ofCodec(ItemStack.CODEC));
            return new HeartAmuletRecipe(s, null, itemstack, nonnulllist);
        }


        public static void write(PacketByteBuf buf, HeartAmuletRecipe heartAmuletRecipe) {
            buf.writeString(heartAmuletRecipe.getGroup());
            buf.writeVarInt(heartAmuletRecipe.getIngredients().size());

            for(Ingredient ingredient : heartAmuletRecipe.getIngredients()) {
                buf.write(Endec.ofCodec(Ingredient.DISALLOW_EMPTY_CODEC), ingredient);
            }
            buf.write(Endec.ofCodec(ItemStack.CODEC),heartAmuletRecipe.getResult(DynamicRegistryManager.EMPTY));
        }

        private static final MapCodec<HeartAmuletRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                                CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category),
                                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                                Ingredient.DISALLOW_EMPTY_CODEC
                                        .listOf()
                                        .fieldOf("ingredients")
                                        .flatXmap(
                                                ingredients -> {
                                                    Ingredient[] ingredients2 = (Ingredient[])ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(i -> new Ingredient[i]);
                                                    if (ingredients2.length == 0) {
                                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                                    } else {
                                                        return ingredients2.length > 9
                                                                ? DataResult.error(() -> "Too many ingredients for shapeless recipe")
                                                                : DataResult.success(DefaultedList.<Ingredient>copyOf(Ingredient.EMPTY, ingredients2));
                                                    }
                                                },
                                                DataResult::success
                                        )
                                        .forGetter(recipe -> recipe.ingredients)
                        )
                        .apply(instance, HeartAmuletRecipe::new)
        );

        @Override
        public MapCodec<HeartAmuletRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, HeartAmuletRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
