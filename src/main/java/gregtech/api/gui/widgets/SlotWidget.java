package gregtech.api.gui.widgets;

import gregtech.api.gui.INativeWidget;
import gregtech.api.gui.IRenderContext;
import gregtech.api.gui.Widget;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotWidget extends Widget implements INativeWidget {

    protected SlotItemHandler slotReference;
    protected boolean isEnabled = true;

    protected boolean canTakeItems;
    protected boolean canPutItems;
    protected SlotLocationInfo locationInfo = new SlotLocationInfo(false, false);

    protected TextureArea[] backgroundTexture;
    protected Runnable changeListener;

    public SlotWidget(IItemHandlerModifiable itemHandler, int slotIndex, int xPosition, int yPosition, boolean canTakeItems, boolean canPutItems) {
        super(new Position(xPosition, yPosition), new Size(18, 18));
        this.canTakeItems = canTakeItems;
        this.canPutItems = canPutItems;
        this.slotReference = new WidgetSlotDelegate(itemHandler, slotIndex, xPosition + 1, yPosition + 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInBackground(int mouseX, int mouseY, IRenderContext context) {
        if (isEnabled && backgroundTexture != null) {
            Position pos = getPosition();
            Size size = getSize();
            for (TextureArea backgroundTexture : this.backgroundTexture) {
                backgroundTexture.draw(pos.x, pos.y, size.width, size.height);
            }
        }
    }

    @Override
    protected void onPositionUpdate() {
        if (slotReference != null) {
            Position position = getPosition();
            this.slotReference.xPos = position.x + 1;
            this.slotReference.yPos = position.y + 1;
        }
    }

    public SlotWidget setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
        return this;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public void detectAndSendChanges() {
    }

    public SlotWidget(IItemHandlerModifiable itemHandler, int slotIndex, int xPosition, int yPosition) {
        this(itemHandler, slotIndex, xPosition, yPosition, true, true);
    }

    /**
     * Sets array of background textures used by slot
     * they are drawn on top of each other
     */
    public SlotWidget setBackgroundTexture(TextureArea... backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }

    public SlotWidget setLocationInfo(boolean isPlayerInventory, boolean isHotbarSlot) {
        this.locationInfo = new SlotLocationInfo(isPlayerInventory, isHotbarSlot);
        return this;
    }

    @Override
    public SlotLocationInfo getSlotLocationInfo() {
        return locationInfo;
    }

    public boolean canPutStack(ItemStack stack) {
        return isEnabled && canPutItems;
    }

    public boolean canTakeStack(EntityPlayer player) {
        return isEnabled && canTakeItems;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack) {
        return isEnabled;
    }

    public void onSlotChanged() {
        gui.holder.markAsDirty();
    }

    @Override
    public ItemStack slotClick(int dragType, ClickType clickTypeIn, EntityPlayer player) {
        return INativeWidget.VANILLA_LOGIC;
    }

    @Override
    public final SlotItemHandler getHandle() {
        return slotReference;
    }

    protected class WidgetSlotDelegate extends SlotItemHandler {

        public WidgetSlotDelegate(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return SlotWidget.this.canPutStack(stack) && super.isItemValid(stack);
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return SlotWidget.this.canTakeStack(playerIn) && super.canTakeStack(playerIn);
        }

        @Override
        public void putStack(@Nonnull ItemStack stack) {
            super.putStack(stack);
            if (changeListener != null) {
                changeListener.run();
            }
        }

        @Override
        public void onSlotChanged() {
            SlotWidget.this.onSlotChanged();
        }

        @Override
        public boolean isEnabled() {
            return SlotWidget.this.isEnabled();
        }
    }

}
