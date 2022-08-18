package ch.so.agi.meta2file;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDate;

import ch.so.agi.meta2file.except.Meta2FileException;
import ch.so.agi.meta2file.model.AttributeInfo;
import ch.so.agi.meta2file.model.ThemePublication;
import ch.so.agi.meta2file.model.FileFormat;
import ch.so.agi.meta2file.model.Office;
import ch.so.agi.meta2file.model.Service;
import ch.so.agi.meta2file.model.ServiceType;
import ch.so.agi.meta2file.model.TableInfo;
import org.junit.jupiter.api.Assertions;

public class TestUtils {
    public static HashMap<String,ThemePublication> getDatasets() {
        var df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        var themePublications = new HashMap<String,ThemePublication>();

        {
            var themePublication = new ThemePublication();
            themePublication.setIdentifier("ch.so.agi.av_gb_administrative_einteilung");
            themePublication.setModel("SO_AGI_AV_GB_Administrative_Einteilungen_Publikation_20180822");
            themePublication.setLastPublishingDate(LocalDate.parse("2022-04-05"));
            themePublication.setTitle("Administrative Einteilungen der amtlichen Vermessung und des Grundbuchs");
            themePublication.setShortDescription("Lorem <b>ipsum</b> dolor sit amet, <blink>consetetur sadipscing</blink> elitr, <a href ='https://de.wikipedia.org/wiki/Rumours'>Warum nur...</a> sed diam nonumy eirmod tempor invidunt ut <acronym title='Founded in 2006'>Twitter</acronym> labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
            themePublication.setKeywordsList(Arrays.asList(new String[]{"AGI","Grundbuch","AS","AV","Amtliche Vermessung","Vermessung","Einteilung"}));
            
            Office owner = new Office();
            owner.setAgencyName("Amt für Umwelt");
            owner.setAbbreviation("AfU");
            owner.setDivision("Ich bin die Abteilung");
            try {
                owner.setOfficeAtWeb(new URI("https://afu.so.ch"));
                owner.setEmail(new URI("mailto:afu@bd.so.ch"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            owner.setPhone("032 627 12 34");
            themePublication.setOwner(owner);

            Office servicer = new Office();
            servicer.setAgencyName("Amt für Geoinformation");
            servicer.setAbbreviation("AGI");
            try {
                servicer.setOfficeAtWeb(new URI("https://agi.so.ch"));
                servicer.setEmail(new URI("mailto:agi@bd.so.ch"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            servicer.setPhone("032 627 75 96");
            themePublication.setServicer(servicer);
            
            try {
                themePublication.setFurtherInformation(new URI("http://google.ch/oder/wikipedia"));
                themePublication.setLicence(new URI("https://www.gl.ch/public/upload/assets/5053/ktgl-ogd-geo-20180601.pdf?fp=1"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            
            List<FileFormat> fileFormats = new ArrayList<>();
            {
                FileFormat fileFormat = new FileFormat();
                fileFormat.setName("INTERLIS");
                fileFormat.setAbbreviation("ili");
                fileFormat.setMimetype("application/xml");
                fileFormats.add(fileFormat);
            }
            {
                FileFormat fileFormat = new FileFormat();
                fileFormat.setName("GeoPackage");
                fileFormat.setAbbreviation("gpkg");
                fileFormat.setMimetype("application/geopackage+sqlite3 ");
                fileFormats.add(fileFormat);
            }        
            themePublication.setFileFormats(fileFormats);
            
            List<TableInfo> tablesInfo = new ArrayList<>();
            {
                var tableInfo = new TableInfo();
                tableInfo.setSqlName("grundbuchkreise_grundbuchkreis");
                tableInfo.setShortDescription("Grundbuchkreisaufteilung inkl. Anschrift etc. der einzelnen Kreise");
                
                List<AttributeInfo> attributesInfo = new ArrayList<>();
                {
                    var attributeInfo = new AttributeInfo();
                    attributeInfo.setName("t_id");
                    attributeInfo.setDatatype("int8");
                    attributeInfo.setMandatory(true);
                    attributesInfo.add(attributeInfo);
                }
                {
                    var attributeInfo = new AttributeInfo();
                    attributeInfo.setName("aname");
                    attributeInfo.setShortDescription("Name des Grundbuches");
                    attributeInfo.setDatatype("text");
                    attributeInfo.setMandatory(true);
                    attributesInfo.add(attributeInfo);
                }
                {
                    var attributeInfo = new AttributeInfo();
                    attributeInfo.setName("perimeter");
                    attributeInfo.setShortDescription("Perimeter des Grundbuchkreises");
                    attributeInfo.setDatatype("polygon");
                    attributeInfo.setMandatory(true);
                    attributesInfo.add(attributeInfo);
                }
                tableInfo.setAttributesInfo(attributesInfo);
                tablesInfo.add(tableInfo);
            }
            themePublication.setTablesInfo(tablesInfo);
            
            List<Service> services = new ArrayList<>();
            {
                var service = new Service();
                try {
                    service.setEndpoint(new URI("https://geo.so.ch/api/wms?SERVICE=WMS&REQUEST=GetCapabilities&VERSION=1.3.0"));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                service.setType(ServiceType.WMS);
                var layers = new ArrayList<String>();
                layers.add("ch.so.awjf.forstreviere.forstkreis");
                layers.add("ch.so.awjf.forstreviere.forstreviere");
                service.setLayerIdentifiers(layers);
                services.add(service);
            }
            {
                var service = new Service();
                try {
                    service.setEndpoint(new URI("https://geo.so.ch/api/wms?SERVICE=WFS&REQUEST=GetCapabilities&VERSION=1.0.0"));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                service.setType(ServiceType.WFS);
                var layers = new ArrayList<String>();
                layers.add("ch.so.awjf.forstreviere.forstkreis");
                service.setLayerIdentifiers(layers);
                services.add(service);
            }
            {
                var service = new Service();
                try {
                    service.setEndpoint(new URI("https://geo.so.ch/api/data/v1/api"));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                service.setType(ServiceType.DATA);
                var layers = new ArrayList<String>();
                layers.add("ch.so.awjf.forstreviere.forstkreis.data");
                layers.add("ch.so.awjf.forstreviere.forstreviere");
                layers.add("ch.so.awjf.forstreviere.fubar");
                service.setLayerIdentifiers(layers);
                services.add(service);
            }
            themePublication.setServices(services);
            themePublications.put(themePublication.getIdentifier(), themePublication);
        }
        
        {
            var themePublication = new ThemePublication();
            themePublication.setIdentifier("ch.so.arp.richtplan");
            themePublication.setModel("SO_AGI_AV_GB_Administrative_Einteilungen_Publikation_20180822");
            themePublication.setLastPublishingDate(LocalDate.parse("2022-04-05"));
            themePublication.setTitle("Administrative Einteilungen der amtlichen Vermessung und des Grundbuchs");
            themePublication.setShortDescription("Lorem <b>ipsum</b> dolor sit amet, <blink>consetetur sadipscing</blink> elitr, <a href ='https://de.wikipedia.org/wiki/Rumours'>Warum nur...</a> sed diam nonumy eirmod tempor invidunt ut <acronym title='Founded in 2006'>Twitter</acronym> labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
            themePublication.setKeywordsList(Arrays.asList(new String[]{"AGI","Grundbuch","AS","AV","Amtliche Vermessung","Vermessung","Einteilung"}));
            
            Office owner = new Office();
            owner.setAgencyName("Amt für Umwelt");
            owner.setAbbreviation("AfU");
            owner.setDivision("Ich bin die Abteilung");
            try {
                owner.setOfficeAtWeb(new URI("https://afu.so.ch"));
                owner.setEmail(new URI("mailto:afu@bd.so.ch"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            owner.setPhone("032 627 12 34");
            themePublication.setOwner(owner);
    
            Office servicer = new Office();
            servicer.setAgencyName("Amt für Geoinformation");
            servicer.setAbbreviation("AGI");
            try {
                servicer.setOfficeAtWeb(new URI("https://agi.so.ch"));
                servicer.setEmail(new URI("mailto:agi@bd.so.ch"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            servicer.setPhone("032 627 75 96");
            themePublication.setServicer(servicer);
            
            try {
                themePublication.setFurtherInformation(new URI("http://google.ch/oder/wikipedia"));
                themePublication.setLicence(new URI("https://www.gl.ch/public/upload/assets/5053/ktgl-ogd-geo-20180601.pdf?fp=1"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            
            List<FileFormat> fileFormats = new ArrayList<>();
            {
                FileFormat fileFormat = new FileFormat();
                fileFormat.setName("INTERLIS");
                fileFormat.setAbbreviation("ili");
                fileFormat.setMimetype("application/xml");
                fileFormats.add(fileFormat);
            }
            {
                FileFormat fileFormat = new FileFormat();
                fileFormat.setName("GeoPackage");
                fileFormat.setAbbreviation("gpkg");
                fileFormat.setMimetype("application/geopackage+sqlite3 ");
                fileFormats.add(fileFormat);
            }        
            themePublication.setFileFormats(fileFormats);
            
            List<TableInfo> tablesInfo = new ArrayList<>();
            {
                var tableInfo = new TableInfo();
                tableInfo.setSqlName("grundbuchkreise_grundbuchkreis");
                tableInfo.setShortDescription("Grundbuchkreisaufteilung inkl. Anschrift etc. der einzelnen Kreise");
                
                List<AttributeInfo> attributesInfo = new ArrayList<>();
                {
                    var attributeInfo = new AttributeInfo();
                    attributeInfo.setName("t_id");
                    attributeInfo.setDatatype("int8");
                    attributeInfo.setMandatory(true);
                    attributesInfo.add(attributeInfo);
                }
                {
                    var attributeInfo = new AttributeInfo();
                    attributeInfo.setName("aname");
                    attributeInfo.setShortDescription("Name des Grundbuches");
                    attributeInfo.setDatatype("text");
                    attributeInfo.setMandatory(true);
                    attributesInfo.add(attributeInfo);
                }
                {
                    var attributeInfo = new AttributeInfo();
                    attributeInfo.setName("perimeter");
                    attributeInfo.setShortDescription("Perimeter des Grundbuchkreises");
                    attributeInfo.setDatatype("polygon");
                    attributeInfo.setMandatory(true);
                    attributesInfo.add(attributeInfo);
                }
                tableInfo.setAttributesInfo(attributesInfo);
                tablesInfo.add(tableInfo);
            }
            themePublication.setTablesInfo(tablesInfo);
            
            List<Service> services = new ArrayList<>();
            {
                var service = new Service();
                try {
                    service.setEndpoint(new URI("https://geo.so.ch/api/wms?SERVICE=WMS&REQUEST=GetCapabilities&VERSION=1.3.0"));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                service.setType(ServiceType.WMS);
                var layers = new ArrayList<String>();
                layers.add("ch.so.awjf.forstreviere.forstkreis");
                layers.add("ch.so.awjf.forstreviere.forstreviere");
                service.setLayerIdentifiers(layers);
                services.add(service);
            }
            themePublication.setServices(services);
            themePublications.put(themePublication.getIdentifier(), themePublication);
        }
        return themePublications;
    }

    public static Path tempFile(String prefix, String suffix){
        Path res = null;

        try {
            res = Files.createTempFile(prefix, suffix);
        } catch (IOException e) {
            throw new Meta2FileException(e);
        }
        return res;
    }

    public static void assertContains(String wholeString, String[] mandatoryParts) {
        ArrayList<String> missing = new ArrayList();

        for(String part : mandatoryParts){
            if(!wholeString.contains(part)){
                missing.add(part);
            }
        }

        Assertions.assertEquals(
                0,
                missing.size(),
                MessageFormat.format("Expected but missing string parts: {0}", missing));
    }
}
