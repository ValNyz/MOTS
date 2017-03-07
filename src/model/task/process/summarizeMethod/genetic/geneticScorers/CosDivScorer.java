package model.task.process.summarizeMethod.genetic.geneticScorers;

import java.util.HashMap;

import exception.LacksOfFeatures;
import model.Model;
import model.task.process.summarizeMethod.genetic.GeneticIndividual;
import textModeling.Corpus;
import textModeling.wordIndex.Dictionnary;
import textModeling.wordIndex.InvertedIndex;

public class CosDivScorer extends GeneticIndividualScorer{

	private CosineScorer cosScorer;
	private DiversityScorer divScorer;
	
	public CosDivScorer(HashMap <GeneticIndividualScorer, Double> scorers, Corpus corpus, HashMap<Integer, String> hashMapWord, InvertedIndex invertedIndex, Dictionnary index, Double divWeight, Double delta, Double firstSentenceConceptsFactor, Integer window, Double fsc_factor) throws LacksOfFeatures {
		super(null, null, hashMapWord, invertedIndex, index, divWeight, null, null, null, null);
		this.cosScorer = new CosineScorer(null, null, null, this.invertedIndex, this.index, null, null, null, null, null);
		this.divScorer = new DiversityScorer(null, null, hashMapWord, this.invertedIndex, this.index, null, null, null, null, null);
		if (divWeight == null)
			throw new LacksOfFeatures("Need feature DivWeight.");
	}
	
	@Override
	public double computeScore(GeneticIndividual gi) {
		return (1-this.divWeight) * cosScorer.computeScore(gi) + this.divWeight * divScorer.computeScore(gi);
	}
	
	
}
