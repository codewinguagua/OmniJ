package foundation.omni.test.consensus

import foundation.omni.CurrencyID
import foundation.omni.Ecosystem
import foundation.omni.consensus.OmniwalletConsensusFetcher
import foundation.omni.rpc.SmartPropertyListInfo

import static foundation.omni.CurrencyID.*
import spock.lang.Specification

/**
 * Functional test for getting consensus data from Omni API
 */
class OmniwalletServerSpec extends Specification {

    def "Can get Omniwallet block height"() {
        setup:
        OmniwalletConsensusFetcher omniFetcher = new OmniwalletConsensusFetcher()

        when: "we get a block height"
        /* Private method, but we can still call it with Groovy for a test */
        def blockHeight = omniFetcher.currentBlockHeight()

        then: "it looks valid"
        /* TODO:  Check to make sure it's within 15 blocks of Omni Core, or something like that? */
        blockHeight >= 315121
    }

    def "Can get Omniwallet consensus data"() {
        setup:
        OmniwalletConsensusFetcher omniFetcher = new OmniwalletConsensusFetcher()

        when: "we get data"
        def omniSnapshot = omniFetcher.getConsensusSnapshot(OMNI)

        then: "something is there"
        omniSnapshot.currencyID == OMNI
        omniSnapshot.blockHeight > 323000  // Greater than a relatively recent main-net block
        omniSnapshot.entries.size() >= 1
    }

    def "Can get Omniwallet property list"() {
        setup:
        OmniwalletConsensusFetcher omniFetcher = new OmniwalletConsensusFetcher()

        when: "we get data"
        def properties = omniFetcher.listProperties()

        then: "we get a list of size >= 2"
        properties != null
        properties.size() >= 2

        when: "we convert the list to a map"
        // This may be unnecessary if we can assume the property list is ordered by propertyid
        Map<CurrencyID, SmartPropertyListInfo> props = properties.collect{[it.propertyid, it]}.collectEntries()

        then: "we can check OMNI and TOMNI are as expected"
        props[OMNI].propertyid == OMNI
        props[OMNI].propertyid.ecosystem == Ecosystem.OMNI
        props[OMNI].name == "Omni" // Note: Omni Core returns "MasterCoin" with a capital-C
        props[OMNI].category == ""
        props[OMNI].subcategory == ""
        props[OMNI].data == ""
        props[OMNI].url == ""
        props[OMNI].divisible == null

        props[TOMNI].propertyid == TOMNI
        props[TOMNI].propertyid.ecosystem == Ecosystem.TOMNI
        props[TOMNI].name == "Test Omni" // Note: Omni Core returns "Mastercoin" with a capital-C
        props[TOMNI].category == ""
        props[TOMNI].subcategory == ""
        props[TOMNI].data == ""
        props[TOMNI].url == ""
        props[TOMNI].divisible == null
    }
}
