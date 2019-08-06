package ar.edu.unlp.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ar.edu.unlp.constants.Filenames;
import ar.edu.unlp.constants.PosTagDic;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.InvalidFormatException;

public class Chunker {

	protected InputStream modelIn = null;
	protected ChunkerModel model = null;
	protected ChunkerME chunker = null;
	
	public Chunker() throws InvalidFormatException, IOException {
		this.modelIn = new FileInputStream(Filenames.OPENNLP_MODEL);
		this.model = new ChunkerModel(modelIn);
		this.chunker = new ChunkerME(model);
	}

	public InputStream getModelIn() {
		return modelIn;
	}

	public void setModelIn(InputStream modelIn) {
		this.modelIn = modelIn;
	}

	public ChunkerModel getModel() {
		return model;
	}

	public void setModel(ChunkerModel model) {
		this.model = model;
	}

	public ChunkerME getChunker() {
		return chunker;
	}

	public void setChunker(ChunkerME chunker) {
		this.chunker = chunker;
	}
	
	public String[] universalPOSChunk(String[] sent, String[] pos) {
		String[] posEn = new String[pos.length];
		for (int i = 0; i < pos.length; i++) {
			posEn[i] = PosTagDic.UPT2PENN.get(pos[i]);
		}

		return chunker.chunk(sent, posEn);
	}
}
