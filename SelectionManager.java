public class SelectionManager {
    private static LeftMenuPanel menuPanel;
    private static GridCanvas canvasPanel;

    public static void registerPanels(LeftMenuPanel menu, GridCanvas canvas) {
        menuPanel = menu;
        canvasPanel = canvas;
    }

    public static void clearSelection() {
        if (menuPanel != null) menuPanel.setSelectedGateType(null);
        if (canvasPanel != null) canvasPanel.clearPlacementPreview();
    }
}
