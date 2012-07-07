package com.github.jsr330.spi.config.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.github.jsr330.spi.TypeConfig;
import com.github.jsr330.spi.config.builder.ConfigBuilder;

/**
 * This is a simple {@link ConfigBuilder}-based SAX configurator.
 */
public class XmlConfig {
    
    static final Logger LOGGER = LoggerFactory.getLogger(XmlConfig.class);
    
    protected Reader source;
    
    public XmlConfig(String json) {
        source = new StringReader(json);
    }
    
    public XmlConfig(File file) throws FileNotFoundException {
        source = new InputStreamReader(new FileInputStream(file));
    }
    
    public XmlConfig(URL source) throws IOException {
        this.source = new InputStreamReader(source.openStream());
    }
    
    public XmlConfig(byte[] json) {
        source = new StringReader(new String(json));
    }
    
    public XmlConfig(byte[] json, Charset charset) {
        source = new StringReader(new String(json, charset));
    }
    
    public XmlConfig(InputStream source) {
        this.source = new InputStreamReader(source);
    }
    
    public XmlConfig(Reader source) {
        this.source = source;
    }
    
    /**
     * Gets the {@link TypeConfig} by parsing the XML file. The correct sequence is ensured by the corresponding XSD file.
     */
    public TypeConfig getConfig(final ClassLoader loader) throws IOException, SAXException {
        final ConfigBuilder builder = new ConfigBuilder();
        XMLReader sax;
        
        if (source != null) {
            try {
                sax = XMLReaderFactory.createXMLReader();
                sax.setContentHandler(new ConfigHandler(loader, builder));
                sax.parse(new InputSource(source));
                
                return builder.build();
            } finally {
                try {
                    source.close();
                } catch (IOException exception) {
                    LOGGER.debug("error while closing reader.", exception);
                }
            }
        }
        
        return null;
    }
    
}
