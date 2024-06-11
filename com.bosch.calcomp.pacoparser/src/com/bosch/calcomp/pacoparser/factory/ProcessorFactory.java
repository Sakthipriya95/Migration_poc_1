package com.bosch.calcomp.pacoparser.factory;

import java.util.Map;

import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.PacoParserObjects;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.tagprocessor.AsciiProcessor;
import com.bosch.calcomp.pacoparser.tagprocessor.AxisPtsProcessor;
import com.bosch.calcomp.pacoparser.tagprocessor.CurveProcessor;
import com.bosch.calcomp.pacoparser.tagprocessor.ITagProcessor;
import com.bosch.calcomp.pacoparser.tagprocessor.MapProcessor;
import com.bosch.calcomp.pacoparser.tagprocessor.SWHistoryEntryProcessor;
import com.bosch.calcomp.pacoparser.tagprocessor.SWHistoryProcessor;
import com.bosch.calcomp.pacoparser.tagprocessor.SWInstanceProcessor;
import com.bosch.calcomp.pacoparser.tagprocessor.ValBlkProcessor;
import com.bosch.calcomp.pacoparser.tagprocessor.ValueProcessor;

/**
 * Revision History<br>
 * 
 * Version      Date            Name                Description<br>
 * 0.1     15-May-2007     Parvathy Unnikrishnan    First Draft<br>
 * 0.2     18-Jun-2007     Parvathy Unnikrishnan    Added exception handling<br>
 * 0.3     12-May-2008     Deepa                    SAC-68, added createSWHistoryProcessor(),<br>
 *                                                  createSWHistoryEntryProcessor()<br>
 */
/**
 * Factory class which creates processors for various tag processing.
 * 
 * @author par7kor
 * 
 */
public final class ProcessorFactory {

    private final PacoParser pacoParser;
    private final PacoParserObjects  pacoParserObjects;

    /**
     * @param pacoParser pacoParser
     * @param pacoParserObjects pacoParserObjects 
     */
    public ProcessorFactory(final PacoParser pacoParser
        , final PacoParserObjects pacoParserObjects
        ) {
      this.pacoParser = pacoParser;
      this.pacoParserObjects = pacoParserObjects;
    }

    /**
     * Factory method which creates an instance of SWInstanceProcessor.
     * 
     * @return ITagProcessor
     */
    public ITagProcessor createSWInstanceProcessor() {
      return new SWInstanceProcessor(this.pacoParser
          , this.pacoParserObjects
          );
    }

    /**
     * Factory method which creates an instance of MapProcessor.
     * 
     * @throws PacoParserException -
     *             exception thrown by paco parser plugin.
     * @return ITagProcessor
     */
    public ITagProcessor createMapProcessor(final PacoParser pacoParser) throws PacoParserException {
        return new MapProcessor(this.pacoParser);
    }

    /**
     * Factory method which creates an instance of CurveProcessor.
     * 
     * @throws PacoParserException -
     *             exception thrown byh paco parser plugin.
     * @return ITagProcessor
     */
    public ITagProcessor createCurveProcessor(final PacoParser pacoParser)
            throws PacoParserException {
        return new CurveProcessor(this.pacoParser);
    }

    /**
     * Factory method which creates an instance of ValBlkProcessor.
     * 
     * @throws PacoParserException -
     *             exception thrown byh paco parser plugin.
     * @return ITagProcessor
     */
    public ITagProcessor createValBlkProcessor(final PacoParser pacoParser)
            throws PacoParserException {
        return new ValBlkProcessor(this.pacoParser);
    }

    /**
     * Factory method which creates an instance of AxisPtsProcessor.
     * 
     * @throws PacoParserException -
     *             exception thrown byh paco parser plugin.
     * @return ITagProcessor
     */
    public ITagProcessor createAxisPtsProcessor(final PacoParser pacoParser)
            throws PacoParserException {
        return new AxisPtsProcessor(this.pacoParser);
    }

    /**
     * Factory method which creates an instance of AsciiProcessor.
     * 
     * @throws PacoParserException -
     *             exception thrown byh paco parser plugin.
     * @return ITagProcessor
     */
    public ITagProcessor createAsciiProcessor(final PacoParser pacoParser)
            throws PacoParserException {
        return new AsciiProcessor(this.pacoParser);
    }

    /**
     * Factory method which creates an instance of ValueProcessor.
     * 
     * @throws PacoParserException -
     *             exception thrown byh paco parser plugin.
     * @return ITagProcessor
     */
    public ITagProcessor createValueProcessor(final PacoParser pacoParser)
            throws PacoParserException {
        return new ValueProcessor(this.pacoParser);
    }
    
    /**
     * Factory method which creates an instance of HistoryProcessor.
     * 
     * @param attributesMap - attributes for SW-CS-HISTORY
     * @return ITagProcessor
     * @throws PacoParserException - exception thrown by the paco parser plugin.
     */
    public static ITagProcessor createSWHistoryProcessor(Map attributesMap)
            throws PacoParserException {
      return new SWHistoryProcessor(attributesMap);
    }
    
    /**
     * Factory method which creates an instance of HistoryEntryProcessor.
     * 
     * @param attributesMap - attributes for SW-CS-ENTRY
     * @return ITagProcessor
     * @throws PacoParserException - exception thrown by the paco parser plugin.
     */
    public static ITagProcessor createSWHistoryEntryProcessor(Map attributesMap)
            throws PacoParserException {
      return new SWHistoryEntryProcessor(attributesMap);
    }
}
