/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackofficetest.widgets.modals.builders;

import com.hybris.cockpitng.components.visjs.network.data.Edge;
import com.hybris.cockpitng.components.visjs.network.data.Network;
import com.hybris.cockpitng.components.visjs.network.data.Node;

import de.hybris.platform.integrationbackoffice.widgets.modals.builders.NetworkDataBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.visualizer.EDMXEntityType;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.visualizer.EDMXSchema;
import de.hybris.platform.servicelayer.ServicelayerTest;

import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NetworkDataBuilderIntegrationTest extends ServicelayerTest
{

	private static final String VIS_TABLE_START = "<table style='border-spacing:5px;'>";
	private static final String VIS_TABLE_END = "</table>";
	private static final String VIS_HEADER_ROW_START = "<tr class='z-listhead'>";
	private static final String VIS_HEADER_COL1_START = "<th style='text-align: left;'>";
	private static final String VIS_HEADER_COL2_START = "<th style='text-align: left;'>";
	private static final String VIS_HEADER_COL_END = "</th>";
	private static final String VIS_HEADER_ROW_END = "</tr>";
	private static final String VIS_ROW_START = "<tr class='z-listitem'>";
	private static final String VIS_ROW_END = "</tr>";
	private static final String VIS_COL1_START = "<td class='z-listcell' style='padding-top:0.5em; text-align:left;'>";
	private static final String VIS_COL1_END = "</td>";
	private static final String VIS_COL2_START = "<td class='z-listcell' style='padding-top:0.5em; text-align:left;'>";
	private static final String VIS_COL2_END = "</td>";
	private static final String VIS_WHITESPACE = "&nbsp;";
	private static String cvNodeLabel = "CatalogVersion             \n---------------------------\nintegrationKey             \nversion          Edm.String\nintegrationKey   Edm.String\ncatalog             Catalog\nterritories         Country\n";
	private static String productNodeLabel = "Product                    Root\n-------------------------------\nintegrationKey                 \ncode                 Edm.String\nintegrationKey       Edm.String\ncatalogVersion   CatalogVersion\n";
	private static String catalogNodeLabel = "Catalog                    \n---------------------------\nintegrationKey             \nid               Edm.String\nintegrationKey   Edm.String\n";
	private static String countryNodeLabel = "Country                    \n---------------------------\nintegrationKey             \nisocode          Edm.String\nintegrationKey   Edm.String\n";
	private InputStream inputStream;
	private NetworkDataBuilder networkDataBuilder;
	private String cvNodeTitleFormat = VIS_TABLE_START + VIS_HEADER_ROW_START + VIS_HEADER_COL1_START + "%s" + VIS_HEADER_COL_END + VIS_HEADER_COL2_START + "%s"
			+ VIS_HEADER_COL_END + VIS_HEADER_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + VIS_WHITESPACE + VIS_COL2_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + "%s" + VIS_COL2_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + "%s" + VIS_COL2_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + "%s" + VIS_COL2_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + "%s" + VIS_COL2_END + VIS_ROW_END
			+ VIS_TABLE_END;

	private String productNodeTitleFormat = VIS_TABLE_START + VIS_HEADER_ROW_START + VIS_HEADER_COL1_START + "%s" + VIS_HEADER_COL_END + VIS_HEADER_COL2_START + "%s"
			+ VIS_HEADER_COL_END + VIS_HEADER_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + VIS_WHITESPACE + VIS_COL2_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + "%s" + VIS_COL2_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + "%s" + VIS_COL2_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + "%s" + VIS_COL2_END + VIS_ROW_END
			+ VIS_TABLE_END;

	private String catalogNodeTitleFormat = VIS_TABLE_START + VIS_HEADER_ROW_START + VIS_HEADER_COL1_START + "%s" + VIS_HEADER_COL_END + VIS_HEADER_COL2_START + VIS_WHITESPACE + VIS_HEADER_COL_END + VIS_HEADER_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + VIS_WHITESPACE + VIS_COL2_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + "%s" + VIS_COL1_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + "%s" + VIS_COL1_END + VIS_ROW_END
			+ VIS_TABLE_END;


	private String countryNodeTitleFormat = VIS_TABLE_START + VIS_HEADER_ROW_START + VIS_HEADER_COL1_START + "%s" + VIS_HEADER_COL_END + VIS_HEADER_COL2_START + VIS_WHITESPACE + VIS_HEADER_COL_END + VIS_HEADER_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + VIS_WHITESPACE + VIS_COL2_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + "%s" + VIS_COL1_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "%s" + VIS_COL1_END + VIS_COL2_START + "%s" + VIS_COL1_END + VIS_ROW_END
			+ VIS_TABLE_END;

	private String countryNodeTitle = VIS_TABLE_START + VIS_HEADER_ROW_START + VIS_HEADER_COL1_START + "Country" + VIS_HEADER_COL_END + VIS_HEADER_COL2_START + VIS_WHITESPACE + VIS_HEADER_COL_END + VIS_HEADER_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "integrationKey" + VIS_COL1_END + VIS_COL2_START + VIS_WHITESPACE + VIS_COL2_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "isocode" + VIS_COL1_END + VIS_COL2_START + "Edm.String" + VIS_COL1_END + VIS_ROW_END
			+ VIS_ROW_START + VIS_COL1_START + "integrationKey" + VIS_COL1_END + VIS_COL2_START + "Edm.String" + VIS_COL1_END + VIS_ROW_END
			+ VIS_TABLE_END;

	public static InputStream loadFile(String fileLocation) throws FileNotFoundException
	{
		ClassLoader classLoader = NetworkDataBuilderIntegrationTest.class.getClassLoader();
		URL url = classLoader.getResource(fileLocation);
		File file = null;
		if (url != null)
		{
			file = new File(url.getFile());
		}
		return new FileInputStream(file);
	}

	public EDMXSchema generateSchemaData() throws FileNotFoundException, JAXBException, XMLStreamException
	{
		inputStream = loadFile("test/text/Product_EDMX.txt");
		String rootNodeName = "Product";
		networkDataBuilder = new NetworkDataBuilder(inputStream, rootNodeName);

		EDMXSchema schema = networkDataBuilder.getSchemaData(inputStream);

		return schema;
	}

	@Test
	public void createNetworkVerifyNodesTest() throws FileNotFoundException, JAXBException, XMLStreamException
	{
		EDMXSchema schema = generateSchemaData();
		Network network = networkDataBuilder.createNetwork(schema);
		Collection<Node> nodes = network.getNodes();

		List<String> expected = new ArrayList<>();
		expected.add(productNodeLabel);
		expected.add(catalogNodeLabel);
		expected.add(cvNodeLabel);
		expected.add(countryNodeLabel);

		List<Node> nodesList = new ArrayList(nodes);
		List<String> actual = new ArrayList<>();

		nodesList.forEach(n -> {
			actual.add(n.getLabel());
		});

		assertEquals(4, actual.size());
		assertTrue(actual.contains(productNodeLabel));
		assertTrue(actual.contains(catalogNodeLabel));
		assertTrue(actual.contains(cvNodeLabel));
		assertTrue(actual.contains(countryNodeLabel));
	}


	@Test
	public void createNetworkVerifyNodesTitles() throws FileNotFoundException, JAXBException, XMLStreamException
	{
		String cvNodeTitle = String.format(cvNodeTitleFormat, "CatalogVersion", VIS_WHITESPACE, "integrationKey", "version",
				"Edm.String", "integrationKey", "Edm.String", "catalog", "Catalog", "territories", "Country");
		String productNodeTitle = String.format(productNodeTitleFormat, "Product", "Root", "integrationKey", "code", "Edm.String",
				"integrationKey", "Edm.String", "catalogVersion", "CatalogVersion");
		String catalogNodeTitle = String.format(catalogNodeTitleFormat, "Catalog", "integrationKey", "id", "Edm.String",
				"integrationKey", "Edm.String");
		String countryNodeTitle = String.format(countryNodeTitleFormat, "Country", "integrationKey", "isocode", "Edm.String",
				"integrationKey", "Edm.String");


		EDMXSchema schema = generateSchemaData();
		Network network = networkDataBuilder.createNetwork(schema);
		Collection<Node> nodes = network.getNodes();

		List<Node> nodesList = new ArrayList(nodes);
		List<String> actualNodeTitles = new ArrayList<>();

		nodesList.forEach(n -> {
			actualNodeTitles.add(n.getTitle());
		});


		assertEquals(4, actualNodeTitles.size());
		assertTrue(actualNodeTitles.contains(cvNodeTitle));
		assertTrue(actualNodeTitles.contains(productNodeTitle));
		assertTrue(actualNodeTitles.contains(catalogNodeTitle));
		assertTrue(actualNodeTitles.contains(countryNodeTitle));
	}

	@Test
	public void nodeMaxLength() throws FileNotFoundException, JAXBException, XMLStreamException
	{
		EDMXSchema schema = generateSchemaData();
		List<EDMXEntityType> edmxEntityTypes = schema.getEdmxEntityTypes();
		assertEquals(4, edmxEntityTypes.size());
		edmxEntityTypes.stream().forEach((edmxEntityType) -> {
			int maxLengthForTheNode = networkDataBuilder.maxLength(edmxEntityType);
			if (edmxEntityType.getName().equals("CatalogVersion"))
			{
				assertEquals(24, maxLengthForTheNode);
			}
			else if (edmxEntityType.getName().equals("Product"))
			{
				assertEquals(28, maxLengthForTheNode);
			}
			else if (edmxEntityType.getName().equals("Catalog"))
			{
				assertEquals(24, maxLengthForTheNode);
			}
			else if (edmxEntityType.getName().equals("Country"))
			{
				assertEquals(24, maxLengthForTheNode);
			}
		});
	}

	@Test
	public void createNetworkVerifyEdgesTest() throws FileNotFoundException, JAXBException, XMLStreamException
	{
		EDMXSchema schema = generateSchemaData();
		Network network = networkDataBuilder.createNetwork(schema);
		Collection<Edge> edges = network.getEdges();
		Collection<Node> nodes = network.getNodes();

		Node productNode;
		Node catalogNode;
		Node cvNode;
		Node countryNode;
		productNode = nodes.stream().filter(node -> node.getLabel().equals(productNodeLabel)).findFirst().get();
		catalogNode = nodes.stream().filter(node -> node.getLabel().equals(catalogNodeLabel)).findFirst().get();
		cvNode = nodes.stream().filter(node -> node.getLabel().equals(cvNodeLabel)).findFirst().get();
		countryNode = nodes.stream().filter(node -> node.getLabel().equals(countryNodeLabel)).findFirst().get();

		List<Edge> edgesList = new ArrayList(edges);

		assertEquals(3, edgesList.size());


		for (Edge edge : edgesList)
		{
			if (edge.getFromNode().equals(productNode))
			{
				assertEquals(cvNode, edge.getToNode());
				assertEquals("0..1", edge.getLabel());
			}
			else if (edge.getFromNode().equals(cvNode) && !edge.getToNode().equals(countryNode))
			{
				assertEquals(catalogNode, edge.getToNode());
				assertEquals("0..1", edge.getLabel());
			}
			else if (edge.getFromNode().equals(cvNode) && !edge.getToNode().equals(catalogNode))
			{
				assertEquals(countryNode, edge.getToNode());
				assertEquals("*", edge.getLabel());
			}
			else
			{
				//Any other condition should result in test failure.
				assertEquals(true, false);
			}
		}

	}


}
