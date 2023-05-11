/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.clauses.solutions.analysis;

import de.featjar.clauses.LiteralList;
import de.featjar.clauses.LiteralList.Order;
import de.featjar.clauses.solutions.combinations.LexicographicIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Calculates statistics regarding t-wise feature coverage of a set of
 * solutions.
 *
 * @author Sebastian Krieter
 */
public class PropInteractionFinder {

    private ConfigurationUpdater updater;
    private ConfigurationVerifyer verifier;
    private LiteralList core;

    private List<LiteralList> succeedingConfs;
    private List<LiteralList> failingConfs;

    private int verifyCounter;

    public void reset() {
        succeedingConfs = new ArrayList<>();
        failingConfs = new ArrayList<>();
    }

    public void setUpdater(ConfigurationUpdater updater) {
        this.updater = updater;
    }

    public void setVerifier(ConfigurationVerifyer verifier) {
        this.verifier = verifier;
    }

    public void setCore(LiteralList core) {
        this.core = core;
    }

    public void addConfigurations(List<LiteralList> configurations) {
        configurations.forEach(this::verify);
    }

    public List<LiteralList> find(int t) {
        int[] tempLiterals = failingConfs.get(0).getLiterals();
        int[] variantLiterals = new int[tempLiterals.length - core.size()];
        int variantLiteralsIndex = 0;
        for (int i = 0; i < tempLiterals.length; i++) {
            int l = tempLiterals[i];
            if (l != 0) {
                variantLiterals[variantLiteralsIndex++] = Math.abs(l);
            }
        }

        final int n = variantLiterals.length;
        final int t2 = (n < t) ? n : t;
        final int pow = (int) Math.pow(2, t2);

        ArrayList<int[]> interactions = new ArrayList<>();

        boolean[][] masks = new boolean[pow][t2];
        for (int i = 0; i < masks.length; i++) {
            boolean[] p = masks[i];
            for (int j = 0; j < t2; j++) {
                p[j] = (i >> j & 1) == 0;
            }
        }

        IntStream.range(0, pow) //
                .forEach(maskIndex -> {
                    boolean[] mask = new boolean[t2];
                    for (int j = 0; j < t2; j++) {
                        mask[j] = (maskIndex >> j & 1) == 0;
                    }
                    int[] literals = new int[t2];

                    LexicographicIterator.stream(t2, n).forEach(combo -> {
                        for (int k = 0; k < t2; k++) {
                            final int l = variantLiterals[(int) combo.elementIndices[k]];
                            literals[k] = mask[k] ? l : -l;
                        }

                        int succ = 0;
                        int fail = 0;
                        for (LiteralList config : failingConfs) {
                            if (config.containsAllLiterals(literals)) {
                                fail++;
                            }
                        }
                        for (LiteralList config : succeedingConfs) {
                            if (config.containsAllLiterals(literals)) {
                                succ++;
                            }
                        }
                        if (fail == 0 && succ == 0) {
                            LiteralList config = updater.complete(List.of(literals), null, null)
                                    .orElse(null);
                            if (config != null) {
                                if (verify(config)) {
                                    succ++;
                                    loop:
                                    for (int[] interaction : interactions) {
                                        for (int i = 0; i < t2; i++) {
                                            if (config.indexOfLiteral(interaction[i]) < 0) {
                                                continue loop;
                                            }
                                        }
                                        interaction[t2 + 1]++;
                                    }
                                } else {
                                    fail++;
                                    loop:
                                    for (int[] interaction : interactions) {
                                        for (int i = 0; i < t2; i++) {
                                            if (config.indexOfLiteral(interaction[i]) < 0) {
                                                continue loop;
                                            }
                                        }
                                        interaction[t2]++;
                                    }
                                }
                            }
                            config = updater.complete(null, List.of(literals), null)
                                    .orElse(null);
                            if (config != null) {
                                if (verify(config)) {
                                    loop:
                                    for (int[] interaction : interactions) {
                                        for (int i = 0; i < t2; i++) {
                                            if (config.indexOfLiteral(interaction[i]) < 0) {
                                                continue loop;
                                            }
                                        }
                                        interaction[t2 + 1]++;
                                    }
                                } else {
                                    loop:
                                    for (int[] interaction : interactions) {
                                        for (int i = 0; i < t2; i++) {
                                            if (config.indexOfLiteral(interaction[i]) < 0) {
                                                continue loop;
                                            }
                                        }
                                        interaction[t2]++;
                                    }
                                }
                            }
                            updater.complete(null, List.of(literals), null).orElse(null);
                            if (config != null) {
                                if (verify(config)) {
                                    loop:
                                    for (int[] interaction : interactions) {
                                        for (int i = 0; i < t2; i++) {
                                            if (config.indexOfLiteral(interaction[i]) < 0) {
                                                continue loop;
                                            }
                                        }
                                        interaction[t2 + 1]++;
                                    }
                                } else {
                                    loop:
                                    for (int[] interaction : interactions) {
                                        for (int i = 0; i < t2; i++) {
                                            if (config.indexOfLiteral(interaction[i]) < 0) {
                                                continue loop;
                                            }
                                        }
                                        interaction[t2]++;
                                    }
                                }
                            }
                        }
                        if (fail != 0 || succ != 0) {
                            int[] interaction = Arrays.copyOf(literals, literals.length + 2);
                            interaction[t2] = fail;
                            interaction[t2 + 1] = succ;
                            interactions.add(interaction);
                        }
                    });
                });

        interactions.stream().forEach(a -> a[t2] = support(a[t2], a[t2 + 1]));
        Collections.sort(interactions, Comparator.comparing(a -> a[t2]));
        double max = interactions.get(0)[t2];
        List<LiteralList> collect = interactions.stream()
                .takeWhile(a -> (a[t2] / max) > 0)
                .map(this::ll)
                .collect(Collectors.toCollection(ArrayList::new));

        for (int i = 0; i < collect.size(); i++) {
            collect.set(i, updater.update(collect.get(i)).orElse(null).removeAll(core));
        }

        //		ArrayList<LiteralList> merge = new ArrayList<>();
        //		for (LiteralList in : collect) {
        //			boolean merged = false;
        //			for (int i = 0; i < merge.size(); i++) {
        //				LiteralList config = updater.complete(null, null, List.of(in.getLiterals(), merge.get(i).getLiterals()))
        //						.orElse(null);
        //				if (config == null) {
        //					merge.set(i, LiteralList.merge(List.of(in, merge.get(i))));
        //					merged = true;
        //				}
        //			}
        //			if (!merged) {
        //				merge.add(in);
        //			}
        //		}
        //
        //		for (int i = 0; i < merge.size(); i++) {
        //			merge.set(i, updater.update(merge.get(i)).orElse(null));
        //		}
        for (int i = 0; i < collect.size(); i++) {
            for (int j = i + 1; j < collect.size(); j++) {
                if (collect.get(j).containsAll(collect.get(i))) {
                    collect.set(i, null);
                    break;
                }
            }
        }
        Collections.sort(collect, Comparator.comparing(a -> a.size()));
        //		Collections.reverse(collect);
        //		for (int i = 0; i < collect.size(); i++) {
        //			for (int j = i + 1; j < collect.size(); j++) {
        //				if (collect.get(i).containsAll(collect.get(j))) {
        //					collect.set(i, null);
        //					break;
        //				}
        //			}
        //		}
        //		for (int i = 0; i < collect.size(); i++) {
        //			for (int j = i + 1; j < collect.size(); j++) {
        //				if (collect.get(j).containsAll(collect.get(i))) {
        //					collect.set(i, null);
        //					break;
        //				}
        //			}
        //		}

        ArrayList<int[]> merge2 = new ArrayList<>();
        for (LiteralList literals : collect) {
            if (literals != null) {
                int succ = 0;
                int fail = 0;
                for (LiteralList config : failingConfs) {
                    if (config.containsAll(literals)) {
                        fail++;
                    }
                }
                for (LiteralList config : succeedingConfs) {
                    if (config.containsAll(literals)) {
                        succ++;
                    }
                }
                int[] interaction = Arrays.copyOf(literals.getLiterals(), literals.size() + 1);
                interaction[literals.size()] = support(fail, succ);
                merge2.add(interaction);
            }
        }

        Collections.sort(merge2, Comparator.comparing(a -> a[a.length - 1]));
        double max2 = merge2.get(0)[t2];
        collect = merge2.stream()
                .takeWhile(a -> (a[t2] / max2) > 0.5)
                .map(this::ll2)
                .collect(Collectors.toList());

        return collect;
    }

    private LiteralList ll(int[] a) {
        return new LiteralList(Arrays.copyOf(a, a.length - 2));
    }

    private LiteralList ll2(int[] a) {
        return new LiteralList(Arrays.copyOf(a, a.length - 1));
    }

    private int support(int fail, int succ) {
        int propFail = fail * succeedingConfs.size();
        int propSucc = succ * failingConfs.size();
        return propSucc - propFail;
    }

    private boolean verify(LiteralList solution) {
        verifyCounter++;
        LiteralList nonCore = solution.removeAll(core);
        nonCore.setOrder(Order.UNORDERED);
        nonCore.setOrder(Order.INDEX);
        if (verifier.test(solution) == 0) {
            succeedingConfs.add(nonCore);
            return true;
        } else {
            failingConfs.add(nonCore);
            return false;
        }
    }

    public int getVerifyCounter() {
        return verifyCounter;
    }
}
