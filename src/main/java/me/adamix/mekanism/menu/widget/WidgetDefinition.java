package me.adamix.mekanism.menu.widget;

public sealed interface WidgetDefinition permits
        ButtonIndicatorWidget, ButtonWidget, IndicatorWidget,
        ItemSlotSupplierWidget, ItemSlotWidget, MultiSlotIndicatorWidget,
        SubMenuWidget {
}
