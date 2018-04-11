package liasd.asadera.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CommandView extends AbstractView {

	private boolean separateConfFile;
	private String confProcessFilePath;
	private String confMultiCorpusFilePath;

	private static final Logger logger = Logger.getLogger("CommandView");

	public CommandView(String confFilePath) {
		super();
		this.separateConfFile = false;
		this.confProcessFilePath = confFilePath;
	}

	public CommandView(String confProcessFilePath, String confMultiCorpusFilePath) {
		super();
		this.separateConfFile = true;
		this.confProcessFilePath = confProcessFilePath;
		this.confMultiCorpusFilePath = confMultiCorpusFilePath;
	}

	@Override
	public void update(Observable o, Object arg) {
		logger.log(Level.INFO, "\n" + (String) arg);
	}

	@Override
	public void display() {
		if (separateConfFile) {
			loadProcessConfiguration(confProcessFilePath);
			loadMultiCorpusConfiguration(confMultiCorpusFilePath);
		} else
			loadConfiguration(confProcessFilePath);
		getCtrl().run();
	}

	@Override
	public void close() {
	}

	public void init() {
		if (separateConfFile) {
			loadProcessConfiguration(confProcessFilePath);
			loadMultiCorpusConfiguration(confMultiCorpusFilePath);
		} else
			loadConfiguration(confProcessFilePath);
	}

	private void loadMultiCorpusConfiguration(String configFilePath) {
		try {
			File fXmlFile = new File(configFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			Element root = doc.getDocumentElement();
			NodeList listTask = root.getChildNodes();

			for (int i = 0; i < listTask.getLength(); i++) {
				if (listTask.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element task = (Element) listTask.item(i);

					getCtrl().notifyTaskChanged(Integer.parseInt(task.getAttribute("ID")));

					NodeList multiCorpusList = task.getElementsByTagName("MULTICORPUS");
					for (int l = 0; l < multiCorpusList.getLength(); l++) {
						if (multiCorpusList.item(l).getNodeType() == Node.ELEMENT_NODE) {
							getCtrl().notifyMultiCorpusChanged();

							NodeList corpusList = task.getElementsByTagName("CORPUS");
							for (int j = 0; j < corpusList.getLength(); j++) {
								if (corpusList.item(j).getNodeType() == Node.ELEMENT_NODE) {
									Element corpus = (Element) corpusList.item(j);
									String summaryInputPath = corpus.getElementsByTagName("SUMMARY_PATH").item(0)
											.getTextContent();
									String corpusInputPath = corpus.getElementsByTagName("INPUT_PATH").item(0)
											.getTextContent();
									NodeList documentList = corpus.getElementsByTagName("DOCUMENT");
									List<String> docNames = new ArrayList<String>();
									for (int k = 0; k < documentList.getLength(); k++) {
										if (documentList.item(k).getNodeType() == Node.ELEMENT_NODE) {
											docNames.add(documentList.item(k).getTextContent());
										}
									}
									NodeList summaryList = corpus.getElementsByTagName("SUMMARY");
									List<String> summaryNames = new ArrayList<String>();
									for (int k = 0; k < summaryList.getLength(); k++) {
										if (summaryList.item(k).getNodeType() == Node.ELEMENT_NODE) {
											summaryNames.add(summaryList.item(k).getTextContent());
										}
									}
									getCtrl().notifyCorpusChanged(summaryInputPath, summaryNames, corpusInputPath,
											docNames);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private void loadProcessConfiguration(String configFilePath) {
		try {
			File fXmlFile = new File(configFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			Element root = doc.getDocumentElement();
			NodeList listTask = root.getChildNodes();

			for (int i = 0; i < listTask.getLength(); i++) {
				if (listTask.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element task = (Element) listTask.item(i);

					getCtrl().notifyTaskChanged(Integer.parseInt(task.getAttribute("ID")));

					getCtrl().notifyLanguageChanged(task.getElementsByTagName("LANGUAGE").item(0).getTextContent());

					getCtrl().notifyOutputPathChanged(task.getElementsByTagName("OUTPUT_PATH").item(0).getTextContent());
					try {
						getCtrl().notifyMultiThreadBoolChanged(Boolean
								.parseBoolean(task.getElementsByTagName("MULTITHREADING").item(0).getTextContent()));
					} catch (Exception e) {
						getCtrl().notifyMultiThreadBoolChanged(false);
					}

					NodeList preProcessList = task.getElementsByTagName("PREPROCESS");
					for (int j = 0; j < preProcessList.getLength(); j++) {
						if (preProcessList.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element preProcess = (Element) preProcessList.item(j);
							getCtrl().notifyProcessOptionChanged(getProcessOptionMap(preProcess));

							getCtrl().notifyPreProcessChanged(preProcess.getAttribute("NAME"));
						}
					}

					NodeList processList = task.getElementsByTagName("PROCESS");
					for (int j = 0; j < processList.getLength(); j++) {
						if (processList.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element process = (Element) processList.item(j);
							getCtrl().notifyProcessOptionChanged(getProcessOptionMap(process));

							getCtrl().notifyProcessChanged(process.getAttribute("NAME"));

							NodeList indexBuilderList = process.getElementsByTagName("INDEX_BUILDER");
							for (int k = 0; k < indexBuilderList.getLength(); k++) {
								if (indexBuilderList.item(k).getNodeType() == Node.ELEMENT_NODE) {
									Element indexBuilder = (Element) process.getElementsByTagName("INDEX_BUILDER")
											.item(k);
									getCtrl().notifyProcessOptionChanged(getProcessOptionMap(indexBuilder));
									getCtrl().notifyIndexBuilderChanged(process.getAttribute("NAME"),
											indexBuilder.getAttribute("NAME"));
								}
							}

							NodeList caracBuilderList = process.getElementsByTagName("CARACTERISTIC_BUILDER");
							for (int k = 0; k < caracBuilderList.getLength(); k++) {
								if (caracBuilderList.item(k).getNodeType() == Node.ELEMENT_NODE) {
									Element caracBuilder = (Element) process.getElementsByTagName("CARACTERISTIC_BUILDER").item(k);
									getCtrl().notifyProcessOptionChanged(getProcessOptionMap(caracBuilder));
									getCtrl().notifyCaracteristicBuilderChanged(process.getAttribute("NAME"),
											caracBuilder.getAttribute("NAME"));
								}
							}

							NodeList scoringMethodList = process.getElementsByTagName("SCORING_METHOD");
							for (int k = 0; k < scoringMethodList.getLength(); k++) {
								if (scoringMethodList.item(k).getNodeType() == Node.ELEMENT_NODE) {
									Element scoringMethod = (Element) process.getElementsByTagName("SCORING_METHOD")
											.item(k);
									getCtrl().notifyProcessOptionChanged(getProcessOptionMap(scoringMethod));
									getCtrl().notifyScoringMethodChanged(process.getAttribute("NAME"),
											scoringMethod.getAttribute("NAME"));
								}
							}

							Element summarizeMethod = (Element) process.getElementsByTagName("SUMMARIZE_METHOD")
									.item(0);
							if (summarizeMethod != null) {
								getCtrl().notifyProcessOptionChanged(getProcessOptionMap(summarizeMethod));
								getCtrl().notifySelectionMethodChanged(process.getAttribute("NAME"),
										summarizeMethod.getAttribute("NAME"));
							}

							NodeList postProcessList = process.getElementsByTagName("POSTPROCESS");
							for (int k = 0; k < postProcessList.getLength(); k++) {
								if (postProcessList.item(k).getNodeType() == Node.ELEMENT_NODE) {
									Element postProcess = (Element) postProcessList.item(k);
									getCtrl().notifyProcessOptionChanged(getProcessOptionMap(postProcess));

									getCtrl().notifyPostProcessChanged(process.getAttribute("NAME"),
											postProcess.getAttribute("NAME"));
								}
							}
						}
					}
					NodeList rougeList = task.getElementsByTagName("ROUGE_EVALUATION");
					if (rougeList.getLength() > 0) {
						getCtrl().notifyRougeEvaluationChanged(true);
						Element rouge = (Element) rougeList.item(0);
						getCtrl().notifyRougeMeasureChanged(
								rouge.getElementsByTagName("ROUGE-MEASURE").item(0).getTextContent());
						getCtrl().notifyRougePathChanged(
								rouge.getElementsByTagName("ROUGE-PATH").item(0).getTextContent());
						getCtrl().notifyModelRootChanged(
								rouge.getElementsByTagName("MODEL-ROOT").item(0).getTextContent());
						getCtrl().notifyPeerRootChanged(
								rouge.getElementsByTagName("PEER-ROOT").item(0).getTextContent());
					} else
						getCtrl().notifyRougeEvaluationChanged(false);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private void loadConfiguration(String configFilePath) {
		try {
			File fXmlFile = new File(configFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			Element racine = doc.getDocumentElement();
			NodeList listTask = racine.getChildNodes();
			for (int i = 0; i < listTask.getLength(); i++) {
				if (listTask.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element task = (Element) listTask.item(i);

					getCtrl().notifyTaskChanged(Integer.parseInt(task.getAttribute("ID")));

					getCtrl().notifyLanguageChanged(task.getElementsByTagName("LANGUAGE").item(0).getTextContent());
					getCtrl().notifyOutputPathChanged(task.getElementsByTagName("OUTPUT_PATH").item(0).getTextContent());
					try {
						getCtrl().notifyMultiThreadBoolChanged(Boolean
								.parseBoolean(task.getElementsByTagName("MULTITHREADING").item(0).getTextContent()));
					} catch (Exception e) {
						getCtrl().notifyMultiThreadBoolChanged(false);
					}

					NodeList multiCorpusList = task.getElementsByTagName("MULTICORPUS");
					for (int l = 0; l < multiCorpusList.getLength(); l++) {
						if (multiCorpusList.item(l).getNodeType() == Node.ELEMENT_NODE) {
							getCtrl().notifyMultiCorpusChanged();

							NodeList corpusList = task.getElementsByTagName("CORPUS");
							for (int j = 0; j < corpusList.getLength(); j++) {
								if (corpusList.item(j).getNodeType() == Node.ELEMENT_NODE) {
									Element corpus = (Element) corpusList.item(j);
									String summaryInputPath = "";
									if (corpus.getElementsByTagName("SUMMARY_PATH").getLength() != 0)
										summaryInputPath = corpus.getElementsByTagName("SUMMARY_PATH").item(0)
											.getTextContent();
									String corpusInputPath = corpus.getElementsByTagName("INPUT_PATH").item(0)
											.getTextContent();
									NodeList documentList = corpus.getElementsByTagName("DOCUMENT");
									List<String> docNames = new ArrayList<String>();
									for (int k = 0; k < documentList.getLength(); k++) {
										if (documentList.item(k).getNodeType() == Node.ELEMENT_NODE) {
											docNames.add(documentList.item(k).getTextContent());
										}
									}
									NodeList summaryList = corpus.getElementsByTagName("SUMMARY");
									List<String> summaryNames = new ArrayList<String>();
									for (int k = 0; k < summaryList.getLength(); k++) {
										if (summaryList.item(k).getNodeType() == Node.ELEMENT_NODE) {
											summaryNames.add(summaryList.item(k).getTextContent());
										}
									}
									getCtrl().notifyCorpusChanged(summaryInputPath, summaryNames, corpusInputPath,
											docNames);
								}
							}
						}
					}

					NodeList preProcessList = task.getElementsByTagName("PREPROCESS");
					for (int j = 0; j < preProcessList.getLength(); j++) {
						if (preProcessList.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element preProcess = (Element) preProcessList.item(j);
							getCtrl().notifyProcessOptionChanged(getProcessOptionMap(preProcess));

							getCtrl().notifyPreProcessChanged(preProcess.getAttribute("NAME"));
						}
					}

					NodeList processList = task.getElementsByTagName("PROCESS");
					for (int j = 0; j < processList.getLength(); j++) {
						if (processList.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element process = (Element) processList.item(j);
							getCtrl().notifyProcessOptionChanged(getProcessOptionMap(process));

							getCtrl().notifyProcessChanged(process.getAttribute("NAME"));

							NodeList indexBuilderList = process.getElementsByTagName("INDEX_BUILDER");
							for (int k = 0; k < indexBuilderList.getLength(); k++) {
								if (indexBuilderList.item(k).getNodeType() == Node.ELEMENT_NODE) {
									Element indexBuilder = (Element) process.getElementsByTagName("INDEX_BUILDER")
											.item(k);
									getCtrl().notifyProcessOptionChanged(getProcessOptionMap(indexBuilder));
									getCtrl().notifyIndexBuilderChanged(process.getAttribute("NAME"),
											indexBuilder.getAttribute("NAME"));
								}
							}

							NodeList caracBuilderList = process.getElementsByTagName("CARACTERISTIC_BUILDER");
							for (int k = 0; k < caracBuilderList.getLength(); k++) {
								if (caracBuilderList.item(k).getNodeType() == Node.ELEMENT_NODE) {
									Element caracBuilder = (Element) process
											.getElementsByTagName("CARACTERISTIC_BUILDER").item(k);
									getCtrl().notifyProcessOptionChanged(getProcessOptionMap(caracBuilder));
									getCtrl().notifyCaracteristicBuilderChanged(process.getAttribute("NAME"),
											caracBuilder.getAttribute("NAME"));
								}
							}

							NodeList scoringMethodList = process.getElementsByTagName("SCORING_METHOD");
							for (int k = 0; k < scoringMethodList.getLength(); k++) {
								if (scoringMethodList.item(k).getNodeType() == Node.ELEMENT_NODE) {
									Element scoringMethod = (Element) process.getElementsByTagName("SCORING_METHOD")
											.item(k);
									getCtrl().notifyProcessOptionChanged(getProcessOptionMap(scoringMethod));
									getCtrl().notifyScoringMethodChanged(process.getAttribute("NAME"),
											scoringMethod.getAttribute("NAME"));
								}
							}

							Element summarizeMethod = (Element) process.getElementsByTagName("SUMMARIZE_METHOD")
									.item(0);
							if (summarizeMethod != null) {
								getCtrl().notifyProcessOptionChanged(getProcessOptionMap(summarizeMethod));
								getCtrl().notifySelectionMethodChanged(process.getAttribute("NAME"),
										summarizeMethod.getAttribute("NAME"));
							}

							NodeList postProcessList = process.getElementsByTagName("POSTPROCESS");
							for (int k = 0; k < postProcessList.getLength(); k++) {
								if (postProcessList.item(k).getNodeType() == Node.ELEMENT_NODE) {
									Element postProcess = (Element) postProcessList.item(k);
									getCtrl().notifyProcessOptionChanged(getProcessOptionMap(postProcess));

									getCtrl().notifyPostProcessChanged(process.getAttribute("NAME"),
											postProcess.getAttribute("NAME"));
								}
							}
						}
					}
					NodeList rougeList = task.getElementsByTagName("ROUGE_EVALUATION");
					if (rougeList.getLength() > 0) {
						getCtrl().notifyRougeEvaluationChanged(true);
						Element rouge = (Element) rougeList.item(0);
						getCtrl().notifyRougeMeasureChanged(
								rouge.getElementsByTagName("ROUGE-MEASURE").item(0).getTextContent());
						getCtrl().notifyRougePathChanged(
								rouge.getElementsByTagName("ROUGE-PATH").item(0).getTextContent());
						getCtrl().notifyModelRootChanged(
								rouge.getElementsByTagName("MODEL-ROOT").item(0).getTextContent());
						getCtrl().notifyPeerRootChanged(
								rouge.getElementsByTagName("PEER-ROOT").item(0).getTextContent());
					} else
						getCtrl().notifyRougeEvaluationChanged(false);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private Map<String, String> getProcessOptionMap(Element element) {
		NodeList optionList = element.getElementsByTagName("OPTION");
		if (optionList.getLength() > 0) {
			Map<String, String> processOption = new HashMap<String, String>();
			for (int k = 0; k < optionList.getLength(); k++) {
				Element option = (Element) optionList.item(k);
				if (option.getParentNode().isEqualNode(element)) {
					processOption.put(option.getAttribute("NAME"), option.getTextContent());
				}
			}
			return processOption;
		} else
			return null;
	}
}