package liasd.asadera.model.task.process.selectionMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import liasd.asadera.model.task.process.processCompatibility.ParametrizedMethod;
import liasd.asadera.model.task.process.processCompatibility.ParametrizedType;
import liasd.asadera.model.task.process.scoringMethod.ScoreBasedIn;
import liasd.asadera.optimize.SupportADNException;
import liasd.asadera.textModeling.Corpus;
import liasd.asadera.textModeling.SentenceModel;
import liasd.asadera.textModeling.Summary;

public class BestIsBetter extends AbstractSelectionMethod implements ScoreBasedIn {

	private Map<SentenceModel, Double> sentencesScore;
	private boolean nbCharSizeOrNbSentenceSize;
	private int maxSummLength;
	private int nbSentenceInSummary;
	
	public BestIsBetter(int id) throws SupportADNException {
		super(id);

		listParameterIn.add(new ParametrizedType(Double.class, Map.class, ScoreBasedIn.class));
	}

	@Override
	public AbstractSelectionMethod makeCopy() throws Exception {
		BestIsBetter p = new BestIsBetter(id);
		initCopy(p);
		return p;
	}

	@Override
	public void initADN() throws Exception {
		nbCharSizeOrNbSentenceSize = Boolean.parseBoolean(getCurrentProcess().getModel().getProcessOption(id, "CharLimitBoolean"));
		int size = Integer.parseInt(getCurrentProcess().getModel().getProcessOption(id, "Size"));
	
		if (nbCharSizeOrNbSentenceSize)
			this.maxSummLength = size;
		else
			this.nbSentenceInSummary = size;
	}

	@Override
	public List<SentenceModel> calculateSummary(List<Corpus> listCorpus) throws Exception {
		Summary summary = new Summary();
		
		List<SentenceModel> list = new ArrayList<SentenceModel>(sentencesScore.keySet());
		Collections.sort(list, (a , b) -> Double.compare(sentencesScore.get(b), sentencesScore.get(a)));
		if (nbCharSizeOrNbSentenceSize) {
			int size = 0;
			Iterator<SentenceModel> senIt = list.iterator();
			while (senIt.hasNext() && size < maxSummLength) {
				SentenceModel sen = senIt.next();
				size+=sen.getNbMot();
				if (size < maxSummLength)
					summary.add(sen);
				else
					size -= sen.getNbMot();
			}
		}
		else {
			int i = 0;
			Iterator<SentenceModel> senIt = list.iterator();
			while (senIt.hasNext() && i < nbSentenceInSummary) {
				summary.add(senIt.next());
				i++;
			}
		}
		double s = 0.0;
		for (SentenceModel sen : summary)
			s += sentencesScore.get(sen);
		System.out.println(s);
		return summary;
	}

	@Override
	public boolean isOutCompatible(ParametrizedMethod compatibleMethod) {
		return false;
	}

	@Override
	public void setCompatibility(ParametrizedMethod compMethod) {
	}

	@Override
	public void setScore(Map<SentenceModel, Double> sentencesScore) {
		this.sentencesScore = sentencesScore;
	}
}
