package com.github.evillootlye.caves.generator.defaults;

public interface OldStructures {
    /*int[][][] rock5 = { { {0, 0, 0}, {0, 0, 0}, {0, 0, 0} },
                        {   {0, 0, 0}, {0, 0, 0}, {0, 0, 0} },
                        {   {0, 0, 0}, {0, 0, 0}, {0, 0, 0} } };*/
    boolean[][][] rock1 = { { {false, true, false},  {false, true, false},  {false, false, false} },
                            { {false, true, true},   {false, true, false},  {false, true, false} },
                            { {false, false, false}, {false, false, false}, {false, false, false} } };
    boolean[][][] rock2 = { { {false, true, false},  {false, true, false},  {false, false, false} },
                            { {true, true, true},    {true, true, true},    {false, true, false} },
                            { {false, true, true},   {false, false, false}, {false, false, false} } };
    boolean[][][] rock3 = { { {false, true, false},  {false, false, false}, {false, false, false} },
                            { {false, true, true},   {false, true, false},  {false, false, false} },
                            { {false, false, false}, {false, false, false}, {false, false, false} } };
    boolean[][][] rock4 = { { {false, true, false},  {false, false, false}, {false, false, false}, {false, false, false} },
                            { {true, true, true},    {false, true, true},   {false, true, true},   {false, true, false} },
                            { {false, true, false},  {false, true, false},  {false, false, false}, {false, false, false} } };
    boolean[][][] rock5 = { { {false, true, false},  {false, false, false}, {false, false, false} },
                            { {true, true, true},    {false, true, true},   {false, false, true} },
                            { {true, true, false},   {false, true, false},  {false, false, false} } };
    boolean[][][] rock6 = { { {true, true, true},    {false, true, true},   {false, true, true},   {false, true, true},   {false, false, true} },
                            { {true, true, true},    {true, true, true},    {true, true, false},   {false, false, false}, {false, false, false} },
                            { {true, true, false},   {false, true, false},  {false, false, false}, {false, false, false}, {false, false, false} } };
    boolean[][][] rock7 = { { {true, true, false},   {true, false, false},  {false, false, false} },
                            { {true, true, true},    {true, true, true},    {true, true, false} },
                            { {true, true, true},    {false, true, false},  {false, false, false} } };
    boolean[][][] rock8 = { { {false, true, false},  {false, false, false}, {false, false, false} },
                            { {true, true, true},    {false, true, false},  {false, false, false} },
                            { {false, true, false},  {false, false, false}, {false, false, false} } };

    //1 == wood decide 2 == chest 3 == torch 4 == random utility 5 == door 6 = wood stay 7 == Random Ore 8 == Snow Block 9 == Spawner 10 = silverfish stone
    int[][][] chests1 = { { {0, 6, 0}, {0, 6, 0}, {0, 0, 0} },
            {   {6, 2, 6}, {6, 0, 0}, {0, 0, 0} },
            {   {0, 5, 0}, {0, 0, 0}, {0, 0, 0} } };
    int[][][] chests2 = { { {1, 1, 1, 1, 1}, {0, 1, 1, 1, 0}, {0, 1, 1, 1, 0}, {0, 1, 1, 1, 0}, {0, 0, 0, 0, 0} },
            {   {1, 1, 1, 1, 1}, {1, 4, 4, 4, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}, {0, 1, 1, 1, 0} },
            {   {1, 1, 1, 1, 1}, {1, 4, 3, 4, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}, {0, 1, 1, 1, 0} },
            {   {1, 1, 1, 1, 1}, {1, 4, 0, 4, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}, {0, 1, 1, 1, 0} },
            {   {1, 1, 1, 1, 1}, {0, 1, 0, 1, 0}, {0, 1, 0, 1, 0}, {0, 1, 1, 1, 0}, {0, 0, 0, 0, 0} } };
    int[][][] chests3 = { { {1, 1, 1}, {0, 0, 0}, {0, 0, 0} },
            {   {1, 1, 1}, {0, 2, 0}, {0, 0, 0} },
            {   {1, 1, 1}, {0, 0, 0}, {0, 0, 0} } };
    int[][][] sfishs1 = { { {0, 7, 0}, {0, 0, 0}, {0, 0, 0} },
            {   {7, 9, 7}, {0, 7, 0}, {0, 0, 0} },
            {   {0, 7, 0}, {0, 0, 0}, {0, 0, 0} } };
    int[][][] sfishs2 = { { {0, 10, 0}, {0, 10, 0}, {0, 0, 0} },
            {   {10, 9, 10}, {0, 8, 0}, {0, 0, 0} },
            {   {0, 10, 8}, {0, 0, 0}, {0, 0, 0} } };
    int[][][] sfishs3 = { { {0, 10, 0}, {0, 10, 0}, {0, 0, 0} },
            {   {10, 9, 10}, {0, 8, 10}, {0, 10, 0} },
            {   {0, 10, 8}, {0, 0, 0}, {0, 0, 0} } };

}
