package pw.smto.bhc.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import pw.smto.bhc.common.BaubleyHeartCanisters;
import pw.smto.bhc.common.Registry;
import pw.smto.bhc.common.util.InventoryUtil;

public class HeartAmuletRecipe extends ShapelessRecipe {

    final String group;
    final ItemStack result;
    final DefaultedList<Ingredient> ingredients;

    public HeartAmuletRecipe(String group,ItemStack stack, DefaultedList<Ingredient> list) {
        super(new Identifier(BaubleyHeartCanisters.MOD_ID, "amulet_shapeless"), group, CraftingRecipeCategory.EQUIPMENT, stack, list);
        this.group = group;
        this.result = stack;
        this.ingredients = list;
    }

    @Override
    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack oldCanister = ItemStack.EMPTY;
        for (int i = 0; i < recipeInputInventory.size(); i++) {
            ItemStack input = recipeInputInventory.getStack(i);
            if(input.getItem() == Registry.Items.HEART_AMULET) {
                oldCanister = input;
                break;
            }
        }
        ItemStack stack = super.craft(recipeInputInventory, dynamicRegistryManager);
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

        @Override
        public HeartAmuletRecipe read(Identifier id, JsonObject json) {
            String type = json.get("type").getAsString();
            ItemStack result = Registries.ITEM.get(new Identifier(json.get("result").getAsJsonObject().get("item").getAsString())).getDefaultStack();
            var ilist = json.get("ingredients").getAsJsonArray();
            DefaultedList<Ingredient> nonnulllist = DefaultedList.ofSize(ilist.size(), Ingredient.EMPTY);
            for(int i = 0; i < ilist.size(); ++i) {
                nonnulllist.set(i, Ingredient.fromJson(ilist.get(i).getAsJsonObject()));
            }
            return new HeartAmuletRecipe(type, result, nonnulllist);
        }

        @Override
        public HeartAmuletRecipe read(Identifier identifier, PacketByteBuf pBuffer) {
            String s = pBuffer.readString();
            int i = pBuffer.readVarInt();
            DefaultedList<Ingredient> nonnulllist = DefaultedList.ofSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromPacket(pBuffer));
            }

            ItemStack itemstack = pBuffer.readItemStack();
            return new HeartAmuletRecipe(s, itemstack, nonnulllist);
        }


        @Override
        public void write(PacketByteBuf buf, HeartAmuletRecipe heartAmuletRecipe) {
            buf.writeString(heartAmuletRecipe.getGroup());
            buf.writeVarInt(heartAmuletRecipe.getIngredients().size());

            for(Ingredient ingredient : heartAmuletRecipe.getIngredients()) {
                ingredient.write(buf);
            }
            buf.writeItemStack(heartAmuletRecipe.getOutput(DynamicRegistryManager.EMPTY));
        }

    }
}
