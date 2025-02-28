package xf.xflp.opt.construction.strategy;

import xf.xflp.base.problem.Container;
import xf.xflp.base.problem.Item;
import xf.xflp.base.problem.Position;
import xf.xflp.base.problem.RotatedPosition;

import java.util.List;

/** 
 * Copyright (c) 2012-present Holger Schneider
 * All rights reserved.
 *
 * This source code is licensed under the MIT License (MIT) found in the
 * LICENSE file in the root directory of this source tree.
 *
 * 
 * @author hschneid
 *
 * The strategy is used in construction heuristic to choose best possible insert position.
 *
 * This type of strategy chooses position, which width is a good proportion of container width.
 * As alternative strategy it uses the TouchingPerimeter
 *
 */
public class WidthProportionFactor extends BaseStrategy {

	private final TouchingPerimeter fallbackStrategy = new TouchingPerimeter();

	@Override
	public Position choose(Item item, Container container, List<Position> posList) {
		if(posList == null || posList.isEmpty()) {
			throw new IllegalStateException("List of positions must be not empty or null.");
		}

		if(posList.size() == 1) {
			return posList.get(0);
		}

		List<Position> filteredPositions = getPositionWithMinValue(
				posList,
				(Position p) -> getDeviationOfProportion(item, p, container)
		);

		if(filteredPositions.size() == 1) {
			return filteredPositions.get(0);
		}

		if(filteredPositions.isEmpty()) {
			throw new IllegalStateException("There must be at least one position.");
		}

		return fallbackStrategy.choose(item,container, filteredPositions);
	}

	private float getDeviationOfProportion(Item i, Position pos, Container container) {
		int conWidth = container.getWidth();
		int itemWidth =  (pos instanceof RotatedPosition) ? i.l : i.w;

		float proportion = conWidth / (float)itemWidth;
		int bestProportion = (int) proportion;
		float deviation = Math.abs(proportion - bestProportion);

		return Math.round(deviation * 10);
	}
}
