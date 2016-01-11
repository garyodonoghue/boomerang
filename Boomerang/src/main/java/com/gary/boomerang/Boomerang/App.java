package com.gary.boomerang.Boomerang;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * The night sky can be modeled as an infinite 2D plane. There are N stars at distinct positions on this plane, the ith of which is at coordinates (Xi, Yi).
 * A boomerang constellation is a pair of distinct equal-length line segments which share a single endpoint, such that both endpoints of each segment coincide with a star's location.
 * Two boomerang constellations are distinct if they're not made up of the same unordered pair of line segments. How many distinct boomerang constellations can you spot?
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {		
		// Read the input file
		FileInputStream fstream = new FileInputStream("boomerang_constellations_example_input.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		int numberOfNights = Integer.parseInt(br.readLine());
		
		for(int i = 0; i<numberOfNights; i++){
			int numBooms = 0;
			int numberOfStars = Integer.parseInt(br.readLine());
			List<Map<Integer, Integer>> points = new ArrayList<>();
			
			for(int k = 0; k<numberOfStars; k++)   {
				String[] tmp = br.readLine().split(" ");
				Map<Integer, Integer> point = new HashMap<>();
				point.put(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
				points.add(point);
			}
			
			// build up mapping between distances and points - e.g. map(dist, ((x1, y1), (x2, y2)))
			// for any equidistant sets of points - see if any of those points are common, this will be a boomerang
			// if there is a common points, increment the number of boomerangs and add those points to a collection of 'used' points
			Multimap<Double, Map<Map<Integer, Integer>, Map<Integer, Integer>>> map = ArrayListMultimap.create();
			
			buildDistMap(map, points);
			
			numBooms = calcNumberBoomerangs(map);
			System.out.println("Case #"+ i + ":" +numBooms);
		}
		
		br.close();
	}

	private static int calcNumberBoomerangs(
			Multimap<Double, Map<Map<Integer, Integer>, Map<Integer, Integer>>> map) {
		
		int numBoomerangs = 0;
		Object[] usedPoints = new Object[map.entries().size()+1];
		
		for(Entry<Double, Map<Map<Integer, Integer>, Map<Integer, Integer>>> mapEntry : map.entries()){
			int counter = 0;
			
			Collection<Map<Map<Integer, Integer>, Map<Integer, Integer>>> sameDistLines = map.get(mapEntry.getKey());
			
			// two or more line segments are the same distance apart and greater than 0 (not the same point), now we must determine whether they share a common point (star) or not
			if(sameDistLines.size() > 1 && mapEntry.getKey() > 0){ 
				
				List<Object> sameDistLinesArr = Arrays.asList(sameDistLines.toArray());
					for(int i = 0; i<sameDistLinesArr.size(); i++){
					
					Map<Map<Integer, Integer>, Map<Integer, Integer>> lines = (Map<Map<Integer, Integer>, Map<Integer, Integer>>) sameDistLinesArr.get(i);
					sameDistLinesLoop: for(Entry<Map<Integer, Integer>, Map<Integer, Integer>> line : lines.entrySet()) {
					
						for(int j = 0; j < sameDistLinesArr.size(); j++){
							
							Map<Map<Integer, Integer>, Map<Integer, Integer>> otherLines = (Map<Map<Integer, Integer>, Map<Integer, Integer>>) sameDistLinesArr.get(j);
							for(Entry<Map<Integer, Integer>, Map<Integer, Integer>> otherLine : otherLines.entrySet()) {
								
								// check if the two lines share a common point, also check if they share two common points, in which case ignore
								if(line.getKey() == otherLine.getKey() && line.getValue() == otherLine.getValue()){
										//they share a second common point, i.e the two lines are overlapping, ignore
										break sameDistLinesLoop;
								}	
								else if(line.getKey() == otherLine.getValue() && line.getValue() == otherLine.getKey()){
										//they share a second common point, i.e the two lines are overlapping, ignore
										break sameDistLinesLoop;
								}
								else if(line.getKey() == otherLine.getValue() && line.getValue() != otherLine.getKey()){
										//the two lines only share a single common point and are equidistant, therefore a candidate for a boomerang constellation!
										
										// cant use the same points in another boomerang
										for(int k = 0 ; k<usedPoints.length; k++){
											if(usedPoints[k] != null){
												List<Map<Integer, Integer>> item = (List<Map<Integer, Integer>>) usedPoints[k];
											
												if(item.contains(line.getKey()) && item.contains(line.getValue()) && item.contains(otherLine.getKey()) && item.contains(otherLine.getValue())){
													// collection of points has already been used to form a boomerang constellation, move onto  next collection of points
													break sameDistLinesLoop;
												}
											}
										}
									
										numBoomerangs++;
										
										//add all the points used for this boomerand to a collection of 'used points'
										List<Map<Integer, Integer>> usedPointsList = new ArrayList<>();
										usedPointsList.add(line.getKey());
										usedPointsList.add(line.getValue());
										usedPointsList.add(otherLine.getKey());
										usedPointsList.add(otherLine.getValue());
										usedPoints[counter] = usedPointsList;
										counter++;
										
								}
								else if(line.getKey() == otherLine.getKey() && line.getValue() != otherLine.getValue()){
									//the two lines only share a single common point and are equidistant, therefore a candidate for a boomerang constellation!
									
									// cant use the same points in another boomerang
									for(int k = 0 ; k<usedPoints.length; k++){
										if(usedPoints[k] != null){
											List<Map<Integer, Integer>> item = (List<Map<Integer, Integer>>) usedPoints[k];
										
											if(item.contains(line.getKey()) && item.contains(line.getValue()) && item.contains(otherLine.getKey()) && item.contains(otherLine.getValue())){
												// collection of points has already been used to form a boomerang constellation, move onto  next collection of points
												break sameDistLinesLoop;
											}
										}
									}
									
									numBoomerangs++;
									
									//add all the points used for this boomerand to a collection of 'used points'
									List<Map<Integer, Integer>> usedPointsList = new ArrayList<>();
									usedPointsList.add(line.getKey());
									usedPointsList.add(line.getValue());
									usedPointsList.add(otherLine.getValue());
									usedPointsList.add(otherLine.getValue());
									usedPoints[counter] = usedPointsList;
									counter++;
								}
							}
						}	
					}
				}
			}
		}
		
		return numBoomerangs;
	}

	private static void buildDistMap(
			Multimap<Double, Map<Map<Integer, Integer>, Map<Integer, Integer>>> map,
			List<Map<Integer, Integer>> points) {
		for(int i = 0; i <= points.size()-1; i++){
			for(int j = 0; j <= points.size()-1; j++){
				Map<Integer, Integer> pt1 = (Map<Integer, Integer>) points.get(i);
				Map<Integer, Integer> pt2 = (Map<Integer, Integer>) points.get(j);
				
				Integer x1 = null, x2 = null, y1 = null, y2 = null;
				
				for (Entry<Integer, Integer> pt1entry : pt1.entrySet()) {
		            x1 = pt1entry.getKey();
		            y1 = pt1entry.getValue();
		        }
				
				for (Entry<Integer, Integer> pt2entry : pt2.entrySet()) {
		            x2 = pt2entry.getKey();
		            y2 = pt2entry.getValue();
		        }
				
				Map<Map<Integer, Integer>, Map<Integer, Integer>> lineMap = new HashMap<>();
				lineMap.put(pt1, pt2);
				
				double dist = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
				map.put(dist, lineMap);
			}
		}
	}
}
