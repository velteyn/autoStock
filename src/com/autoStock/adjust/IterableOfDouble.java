package com.autoStock.adjust;
import com.autoStock.tools.MathTools;


/**
 * @author Kevin Kowalewski
 *
 */
public class IterableOfDouble extends IterableBase {
	private double min;
	private double max;
	private double step;
	
	public IterableOfDouble(double min, double max, double step){
		this.min = min;
		this.max = max;
		this.step = step;
		
		if ((min - max) % step != 0){
//			throw new IllegalArgumentException();
		}
	}
	
	public double getDouble(){
		return MathTools.round((min + (step * currentIndex)));
	}

	@Override
	public boolean hasMore() {
		return currentIndex <= getMaxIndex();
	}

	@Override
	public void reset() {
		super.reset();
	}

	@Override
	public int getMaxIndex() {
		return (int) ((int) (max - min) / step);
	}

	@Override
	public int getMaxValues() {
		return getMaxIndex() + 1;
	}

	@Override
	public boolean isDone() {
		return currentIndex == getMaxIndex();
	}

	@Override
	public boolean skip() {
		return false;
	}
}
