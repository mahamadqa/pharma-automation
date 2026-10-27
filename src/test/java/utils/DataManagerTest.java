package utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DataManagerTest {

    @Test
    public void testDataManagerLocalizationDataLoading() {
        // Set region property to localization
        System.setProperty("region", "localization");
        DataManager.initData();

        // 1. Verify PO data from purchase_order_localization.properties
        String poOrg = DataManager.getData("org");
        Assert.assertEquals(poOrg, "PharmaVerge ERP", "PO org should match");

        String poDocType = DataManager.getData("targetDocType");
        Assert.assertEquals(poDocType, "Purchase Order", "PO targetDocType should match");

        String poBPartner = DataManager.getData("bPartner");
        Assert.assertEquals(poBPartner, "ENIVID LIFE SCIENCES", "PO bPartner should match");

        String poProduct = DataManager.getData("product");
        Assert.assertEquals(poProduct, "Capsule Size - 2 (T.Pink / Clear)1_102CAPS021", "PO product should match");

        // 2. Verify SO data from sales_order_localization.properties
        String soOrg = DataManager.getData("so.org");
        Assert.assertEquals(soOrg, "PharmaVerge ERP", "SO org should match");

        String fallbackOrg = DataManager.getData("so.org", "org");
        Assert.assertEquals(fallbackOrg, "PharmaVerge ERP", "SO fallback org should match");

        String soDocType = DataManager.getData("so.targetDocType");
        Assert.assertEquals(soDocType, "Standard Order", "SO targetDocType should match");

        String soProduct = DataManager.getData("so.product");
        Assert.assertEquals(soProduct, "DICLOVERGE TAB (PINK)_301DICL01", "SO product should match");

        String soFreeQty = DataManager.getData("so.freeQty", "freeQty");
        Assert.assertEquals(soFreeQty, "30", "SO freeQty should match");

        // 3. Verify SOP data from sop_master_localization.properties
        String sopDocType = DataManager.getOrDefault("sop.docType", "DEFAULT");
        Assert.assertEquals(sopDocType, "SOP", "SOP docType should match");

        String sopDesc = DataManager.getOrDefault("sop.description", "DEFAULT");
        Assert.assertEquals(sopDesc, "ANNUAL BUILDING MAINTENANCE", "SOP description should match");
    }

    @Test
    public void testDataManagerNonLocalizationDataLoading() {
        System.setProperty("region", "non-localization");
        DataManager.initData();

        String poBPartner = DataManager.getData("bPartner");
        Assert.assertEquals(poBPartner, "VIRDEV INTERNATIONAL USA", "Non-loc PO bPartner should match");

        String poCurrency = DataManager.getData("currency");
        Assert.assertEquals(poCurrency, "USD", "Non-loc PO currency should match");

        String soCurrency = DataManager.getData("so.currency", "currency");
        Assert.assertEquals(soCurrency, "USD", "Non-loc SO currency should match");

        // Reset back to localization
        System.setProperty("region", "localization");
        DataManager.initData();
    }

    @Test
    public void testDynamicDmsFilePathResolution() {
        String fileName = "Training_Completion_Report.pdf";
        String resolvedFileUtils = FileUtils.getDynamicDmsFilePath(fileName);
        Assert.assertNotNull(resolvedFileUtils, "Resolved path should not be null");
        Assert.assertTrue(new java.io.File(resolvedFileUtils).exists(),
                "Resolved file path should exist on disk: " + resolvedFileUtils);

        String resolvedDocExplorer = pages.DocumentExplorer.getDynamicDmsFilePath(fileName);
        Assert.assertEquals(resolvedDocExplorer, resolvedFileUtils,
                "DocumentExplorer delegate should resolve the exact same path as FileUtils");
    }
}
