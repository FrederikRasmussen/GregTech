package gregtech.common.metatileentities.steam;

import gregtech.api.gui.IUIHolder;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.gui.widgets.ProgressWidget.MoveType;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.SteamMetaTileEntity;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.render.Textures;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class SteamFurnace extends SteamMetaTileEntity {

    public SteamFurnace(String metaTileEntityId, boolean isHighPressure) {
        super(metaTileEntityId, RecipeMaps.FURNACE_RECIPES, Textures.FURNACE_OVERLAY, isHighPressure);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new SteamFurnace(metaTileEntityId, isHighPressure);
    }

    @Override
    protected boolean isBrickedCasing() {
        return true;
    }

    @Override
    public IItemHandlerModifiable createImportItemHandler() {
        return new ItemStackHandler(1);
    }

    @Override
    public IItemHandlerModifiable createExportItemHandler() {
        return new ItemStackHandler(1);
    }

    @Override
    public ModularUI<IUIHolder> createUI(EntityPlayer player) {
        return createUITemplate(player)
            .widget(101, new SlotWidget<>(this.importItems, 0, 53, 25)
                .setBackgroundTexture(BRONZE_SLOT_BACKGROUND_TEXTURE, getFullGuiTexture("slot_%s_furnace_background")))
            .widget(102, new ProgressWidget<>(workableHandler::getProgressPercent, 78, 25, 20, 16)
                .setProgressBar(getFullGuiTexture("progress_bar_%s_furnace"),
                    getFullGuiTexture("progress_bar_%s_furnace_filled"),
                    MoveType.HORIZONTAL))
            .widget(103, new SlotWidget<>(this.exportItems, 0, 107, 25, true, false)
                .setBackgroundTexture(BRONZE_SLOT_BACKGROUND_TEXTURE))
            .build(getHolder(), player);
    }
}
