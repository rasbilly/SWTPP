--- module (NICHT AENDERN!)
module CannonBot where
--- imports (NICHT AENDERN!)
import Data.Char
import Util

--- external signatures (NICHT AENDERN!)
getMove :: String -> String
listMoves :: String -> String

--- YOUR IMPLEMENTATION STARTS HERE ---

getMove s = drop 1 (take 6 (listMoves s))
listMoves s | null (wc) && b = "[b9-b9,c9-c9,d9-d9,e9-e9,f9-f9,g9-g9,h9-h9,i9-i9]"
            | null (bc) && not (b) = "[b0-b0,c0-c0,d0-d0,e0-e0,f0-f0,g0-g0,h0-h0,i0-i0]"
            | otherwise = "[" ++ (allMovesToString (allMoves s)) ++ "]"
            where (wc,bc) = listCities a
                  (a,b) = buildBoard s

buildBoard :: String -> ([[Int]],Bool)
buildBoard s = (map row (map replaceEmpty (take 9 x ++ take 1 (splitOn " " (last x)))),whitesTurn (last (x))) where x = splitOn "/" s

whitesTurn :: String -> Bool
whitesTurn s = if last (splitOn " " s) == "w" then True else False

row :: String -> [Int]
row [] = []
row (x:xs) | x=='w' = [1] ++ row xs
           | x=='W' = [2] ++ (row xs)
           | x=='b' = [-1] ++ (row xs)
           | x=='B' = [-2] ++ (row xs)
           | elem (read [x]) [1..10] = (take (read [x]) (repeat 0)) ++ row xs

replaceEmpty :: String -> String
replaceEmpty s = if null s then "55" else s

addListTuples :: ([a],[a]) -> ([a],[a]) -> ([a],[a])
addListTuples (a,b) (c,d) = (a++c,b++d)

mapToCoordinate :: Int -> [Int] -> [(Int,Int)]
mapToCoordinate x xs = [(x,a) | a<-xs]

listSoldiersRow :: [Int] -> Int -> [Int]
listSoldiersRow [] _ = []
listSoldiersRow (x:xs) s = if x/=s then listSoldiersRow xs s else [9 - (length xs)] ++ listSoldiersRow xs s

listSoldiers :: [[Int]] -> ([(Int,Int)],[(Int,Int)])
listSoldiers [] = ([],[])
listSoldiers (xs:xss) = addListTuples (mapToCoordinate (9 - length xss) (listSoldiersRow xs 1),mapToCoordinate (9 - length xss) (listSoldiersRow xs (-1))) (listSoldiers xss)

soldiers s = listSoldiers a where (a,_) = buildBoard s

listCities :: [[Int]] -> ([(Int,Int)],[(Int,Int)])
listCities [] = ([],[])
listCities (xs:xss) = addListTuples (mapToCoordinate (9 - length xss) (listSoldiersRow xs 2),mapToCoordinate (9 - length xss) (listSoldiersRow xs (-2))) (listCities xss)

cityTuple :: ([(Int,Int)],[(Int,Int)]) -> ((Int,Int),(Int,Int))
cityTuple (a,b) = (head a, head b)

cities2 s = listCities a where (a,_) = buildBoard s
cities s = cityTuple (cities2 s)

following3Ascending :: Int -> Int -> Int -> Bool
following3Ascending x y z = if (x + 1) == y && (y + 1) == z then True else False

following3Descending :: Int -> Int -> Int -> Bool
following3Descending x y z = if (x - 1) == y && (y - 1) == z then True else False

allEqual :: Eq a => [a] -> Bool
allEqual s = not (any (/=x) s) where x = head s

sameTriple :: Eq a => (a,a,a) -> (a,a,a) -> Bool
sameTriple (a,b,c) (d,e,f) = if (a==d && b==e && c==f) || (a==d && b==f && c==e) || (a==e && b==d && c==f) || (a==e && b==f && c==d) || (a==f && b==d && c==e) || (a==f && b==e && c==d) then True else False

containsTriple :: Eq a => (a,a,a) -> [(a,a,a)] -> Bool
containsTriple x xs = any (sameTriple x) xs

containsTuple :: Eq a => (a,a) -> [(a,a)] -> Bool
containsTuple x xs = any (==x) xs

isCannon :: ((Int,Int),(Int,Int),(Int,Int)) -> Bool
isCannon w = (following3Ascending y1 y2 y3 && allEqual [x1,x2,x3]) || (following3Ascending y1 y2 y3 && following3Ascending x1 x2 x3) || (following3Ascending y1 y2 y3 && following3Descending x1 x2 x3) || (allEqual [y1,y2,y3] && following3Ascending x1 x2 x3) where ((y1,x1),(y2,x2),(y3,x3)) = w

allPossibilities :: ([(Int,Int)],[(Int,Int)]) -> ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))])
allPossibilities (a,b) = ([(x,y,z) | x<-a,y<-a,z<-a, x/=y,y/=z,x/=z],[(x,y,z) | x<-b,y<-b,z<-b, x/=y,y/=z,x/=z])

removeDuplicateCannons :: ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))]) -> ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))])
removeDuplicateCannons ([],[]) = ([],[])
removeDuplicateCannons (w:ws,bs) | containsTriple w ws = removeDuplicateCannons (ws,bs)
                                 | otherwise = addListTuples ([w],[]) (removeDuplicateCannons (ws,bs))
removeDuplicateCannons (ws,b:bs) | containsTriple b bs = removeDuplicateCannons (ws,bs)
                                 | otherwise = addListTuples ([],[b]) (removeDuplicateCannons (ws,bs))

listCannons :: ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))]) -> ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))])
listCannons (ws,bs) = ([a | a<-ws, isCannon a],[a | a<-bs, isCannon a])

cannons s =  listCannons (allPossibilities (listSoldiers a)) where (a,_) = buildBoard s

whichList :: ([a],[a]) -> Bool -> [a]
whichList (x,y) b = if b then x else y

validCell :: (Int,Int) -> [(Int,Int)]
validCell (y,x) = if x>=0 && x<=9 && y>=0 && y<=9 then [(y,x)] else []

listAllNeighborCells :: (Int,Int) -> [(Int,Int)]
listAllNeighborCells (y,x) = validCell (y-1,x-1) ++ validCell (y-1,x) ++ validCell (y-1,x+1) ++ validCell (y,x-1) ++ validCell (y,x+1) ++ validCell (y+1,x-1) ++ validCell (y+1,x-1) ++ validCell (y+1,x+1)

listTwoNeighborCells :: (Int,Int) -> [(Int,Int)]
listTwoNeighborCells (y,x) = validCell (y,x-1) ++ validCell (y,x+1)

listThreeNeighborCells :: (Int,Int) -> Bool -> [(Int,Int)]
listThreeNeighborCells (y,x) b | b         = validCell (y+1,x-1) ++ validCell (y+1,x) ++ validCell (y+1,x+1)
                       | otherwise = validCell (y-1,x-1) ++ validCell (y-1,x) ++ validCell (y-1,x+1)

listSteps :: ([(Int,Int)],[(Int,Int)]) -> Bool -> [((Int,Int),(Int,Int))]
listSteps ([],[]) _ = []
listSteps ([],b:bs) playerIsWhite = if playerIsWhite then []       else [(b,t) | t<-listThreeNeighborCells b playerIsWhite, not (containsTuple t bs)] ++ listSteps ([],bs) playerIsWhite
listSteps (w:ws,[]) playerIsWhite = if not (playerIsWhite) then [] else [(w,t) | t<-listThreeNeighborCells w playerIsWhite, not (containsTuple t ws)] ++ listSteps (ws,[]) playerIsWhite
listSteps (w:ws,b:bs) playerIsWhite = if playerIsWhite
                                      then [(w,t) | t<-listThreeNeighborCells w playerIsWhite, not (containsTuple t ws)] ++ [(w,t) | t<-listTwoNeighborCells w, containsTuple t ([b] ++ bs)] ++ listSteps (ws,b:bs) playerIsWhite
                                      else [(b,t) | t<-listThreeNeighborCells b playerIsWhite, not (containsTuple t bs)] ++ [(b,t) | t<-listTwoNeighborCells b, containsTuple t ([b] ++ ws)] ++ listSteps (w:ws,bs) playerIsWhite

steps s = listSteps soldiersList playerIsWhite
  where soldiersList = listSoldiers board
        (board,playerIsWhite) = buildBoard s

listBackupCells :: (Int,Int) -> Bool -> [(Int,Int)]
listBackupCells (y,x) b | b = validCell (y-2,x-2) ++ validCell (y-2,x) ++ validCell (y-2,x+2)
                        | otherwise = validCell (y+2,x-2) ++ validCell (y+2,x) ++ validCell (y+2,x+2)

intersect :: Eq a => [a] -> [a] -> [a]
intersect [] _     =  []
intersect _  []    =  []
intersect xs ys    =  [x | x <- xs, any (== x) ys]

isThreatened :: (Int,Int) -> [(Int,Int)] -> Bool
isThreatened t l = not (null (intersect (listAllNeighborCells t) l))

middle2 :: (Int,Int) -> (Int,Int) -> (Int,Int)
middle2 (a,b) (c,d) | a==c && b==d = (a,b)
                   | a==c && b<d = (a,b+1)
                   | a==c && b>d = (a,b-1)
                   | a<c && b==d = (a+1,b)
                   | a<c && b<d = (a+1,b+1)
                   | a<c && b>d = (a+1,b-1)
                   | a>c && b==d = (a-1,b)
                   | a>c && b<d = (a-1,b+1)
                   | a>c && b>d = (a-1,b-1)

listBackups :: ([(Int,Int)],[(Int,Int)]) -> ([(Int,Int)],[(Int,Int)]) -> ((Int,Int),(Int,Int)) -> Bool -> [((Int,Int),(Int,Int))]
listBackups ([],[]) _ _ _ = []
listBackups (_,[]) _ _ _ = []
listBackups ([],_) _ _ _ = []
listBackups (w:ws,b:bs) (ww,bb) (wc,bc) playerIsWhite = if playerIsWhite
                                      then if not (isThreatened w (bb)) then listBackups (ws,b:bs) (ww,bb) (wc,bc) playerIsWhite else [(w,t) | t<-listBackupCells w playerIsWhite, not (containsTuple (middle2 w t) ww), not (containsTuple (middle2 w t) (bb)), not (containsTuple t ww), not (containsTuple t bb), t/=wc] ++ listBackups (ws,b:bs) (ww,bb) (wc,bc) playerIsWhite
                                      else if not (isThreatened b (ww)) then listBackups (w:ws,bs) (ww,bb) (wc,bc) playerIsWhite else [(b,t) | t<-listBackupCells b playerIsWhite, not (containsTuple (middle2 b t) bb), not (containsTuple (middle2 b t) (ww)), not (containsTuple t ww), not (containsTuple t bb), t/=bc] ++ listBackups (w:ws,bs) (ww,bb) (wc,bc) playerIsWhite

backups s = listBackups soldiersList soldiersList cs playerIsWhite
  where soldiersList = listSoldiers board
        (board,playerIsWhite) = buildBoard s
        cs = cities s

getCannonHead :: ((Int,Int),(Int,Int),(Int,Int)) -> [(Int,Int)]
getCannonHead ((y1,x1),(y2,x2),(y3,x3)) | (following3Ascending y1 y2 y3 && allEqual [x1,x2,x3]) = validCell (y1-1,x1)
                                            | (following3Ascending y1 y2 y3 && following3Ascending x1 x2 x3) = validCell (y1-1,x1-1)
                                            | (following3Ascending y1 y2 y3 && following3Descending x1 x2 x3) = validCell (y1-1,x1+1)
                                            | (allEqual [y1,y2,y3] && following3Ascending x1 x2 x3) = validCell (y1,x1-1)

getCannonTail :: ((Int,Int),(Int,Int),(Int,Int)) -> [(Int,Int)]
getCannonTail ((y1,x1),(y2,x2),(y3,x3)) | (following3Ascending y1 y2 y3 && allEqual [x1,x2,x3]) = validCell (y3+1,x3)
                                            | (following3Ascending y1 y2 y3 && following3Ascending x1 x2 x3) = validCell (y3+1,x3+1)
                                            | (following3Ascending y1 y2 y3 && following3Descending x1 x2 x3) = validCell (y3+1,x3-1)
                                            | (allEqual [y1,y2,y3] && following3Ascending x1 x2 x3) = validCell (y3,x3+1)

getCannonHeadShot :: ((Int,Int),(Int,Int),(Int,Int)) -> [(Int,Int)]
getCannonHeadShot ((y1,x1),(y2,x2),(y3,x3)) | (following3Ascending y1 y2 y3 && allEqual [x1,x2,x3]) = validCell (y1-2,x1) ++ validCell (y1-3,x1)
                                            | (following3Ascending y1 y2 y3 && following3Ascending x1 x2 x3) = validCell (y1-2,x1-2) ++ validCell (y1-3,x1-3)
                                            | (following3Ascending y1 y2 y3 && following3Descending x1 x2 x3) = validCell (y1-2,x1+2) ++ validCell (y1-3,x1+3)
                                            | (allEqual [y1,y2,y3] && following3Ascending x1 x2 x3) = validCell (y1,x1-2) ++ validCell (y1,x1-3)

getCannonTailShot :: ((Int,Int),(Int,Int),(Int,Int)) -> [(Int,Int)]
getCannonTailShot ((y1,x1),(y2,x2),(y3,x3)) | (following3Ascending y1 y2 y3 && allEqual [x1,x2,x3]) = validCell (y3+2,x3) ++ validCell (y3+3,x3)
                                            | (following3Ascending y1 y2 y3 && following3Ascending x1 x2 x3) = validCell (y3+2,x3+2) ++ validCell (y3+3,x3+3)
                                            | (following3Ascending y1 y2 y3 && following3Descending x1 x2 x3) = validCell (y3+2,x3-2) ++ validCell (y3+3,x3-3)
                                            | (allEqual [y1,y2,y3] && following3Ascending x1 x2 x3) = validCell (y3,x3+2) ++ validCell (y3,x3+3)

listCannonMoves :: ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))]) -> ([(Int,Int)],[(Int,Int)]) -> ((Int,Int),(Int,Int)) -> Bool -> [((Int,Int),(Int,Int))]
listCannonMoves ([],[]) _ _ _ = []
listCannonMoves ((a,b,c):ws,[]) (ww,bb) (wc,bc) playerIsWhite = if not (playerIsWhite) then [] else [(c,t) | t<-getCannonHead (a,b,c), not (containsTuple t ww), not (containsTuple t bb), t/=wc, t/=bc] ++ [(a,t) | t<-getCannonTail (a,b,c), not (containsTuple t ww), not (containsTuple t bb), t/=wc, t/=bc] ++ listCannonMoves (ws,[]) (ww,bb) (wc,bc) playerIsWhite
listCannonMoves ([],(d,e,f):bs) (ww,bb) (wc,bc) playerIsWhite = if playerIsWhite       then [] else [(f,t) | t<-getCannonHead (d,e,f), not (containsTuple t ww), not (containsTuple t bb), t/=wc, t/=bc] ++ [(d,t) | t<-getCannonTail (d,e,f), not (containsTuple t ww), not (containsTuple t bb), t/=wc, t/=bc] ++ listCannonMoves ([],bs) (ww,bb) (wc,bc) playerIsWhite
listCannonMoves ((a,b,c):ws,(d,e,f):bs) (ww,bb) (wc,bc) playerIsWhite = if playerIsWhite
    then [(c,t) | t<-getCannonHead (a,b,c), not (containsTuple t ww), not (containsTuple t bb), t/=wc, t/=bc] ++ [(a,t) | t<-getCannonTail (a,b,c), not (containsTuple t ww), not (containsTuple t bb), t/=wc, t/=bc] ++ listCannonMoves (ws,(d,e,f):bs) (ww,bb) (wc,bc) playerIsWhite
    else [(f,t) | t<-getCannonHead (d,e,f), not (containsTuple t ww), not (containsTuple t bb), t/=wc, t/=bc] ++ [(d,t) | t<-getCannonTail (d,e,f), not (containsTuple t ww), not (containsTuple t bb), t/=wc, t/=bc] ++ listCannonMoves ((a,b,c):ws,bs) (ww,bb) (wc,bc) playerIsWhite

cannonMoves s = listCannonMoves (cannons s) (listSoldiers a) (cities s) b where (a,b) = buildBoard s

listCannonShots :: ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))]) -> ([(Int,Int)],[(Int,Int)]) -> ((Int,Int),(Int,Int)) -> Bool -> [((Int,Int),(Int,Int))]
listCannonShots ([],[]) _ _ _ = []
listCannonShots ((a,b,c):ws,[]) (ww,bb) (wc,bc) playerIsWhite = if not (playerIsWhite) then [] else [(c,t) | t<-getCannonHeadShot (a,b,c), (containsTuple t bb) || (t==bc), not (null (getCannonHead (a,b,c))), not (containsTuple (head (getCannonHead (a,b,c))) ww), not (containsTuple (head (getCannonHead (a,b,c))) bb)] ++ [(a,t) | t<-getCannonTailShot (a,b,c), (containsTuple t bb) || (t==bc), not (null (getCannonTail (a,b,c))), not (containsTuple (head (getCannonTail (a,b,c))) ww), not (containsTuple (head (getCannonTail (a,b,c))) bb)] ++ listCannonShots (ws,[]) (ww,bb) (wc,bc) playerIsWhite
listCannonShots ([],(d,e,f):bs) (ww,bb) (wc,bc) playerIsWhite = if playerIsWhite       then [] else [(f,t) | t<-getCannonHeadShot (d,e,f), (containsTuple t ww) || (t==wc), not (null (getCannonHead (d,e,f))), not (containsTuple (head (getCannonHead (d,e,f))) ww), not (containsTuple (head (getCannonHead (d,e,f))) bb)] ++ [(d,t) | t<-getCannonTailShot (d,e,f), (containsTuple t ww) || (t==wc), not (null (getCannonTail (d,e,f))), not (containsTuple (head (getCannonTail (d,e,f))) ww), not (containsTuple (head (getCannonTail (d,e,f))) bb)] ++ listCannonShots ([],bs) (ww,bb) (wc,bc) playerIsWhite
listCannonShots ((a,b,c):ws,(d,e,f):bs) (ww,bb) (wc,bc) playerIsWhite = if playerIsWhite
    then [(c,t) | t<-getCannonHeadShot (a,b,c), (containsTuple t bb) || (t==bc), not (null (getCannonHead (a,b,c))), not (containsTuple (head (getCannonHead (a,b,c))) ww), not (containsTuple (head (getCannonHead (a,b,c))) bb)] ++ [(a,t) | t<-getCannonTailShot (a,b,c), (containsTuple t bb) || (t==bc), not (null (getCannonTail (a,b,c))), not (containsTuple (head (getCannonTail (a,b,c))) ww), not (containsTuple (head (getCannonTail (a,b,c))) bb)] ++ listCannonShots (ws,(d,e,f):bs) (ww,bb) (wc,bc) playerIsWhite
    else [(f,t) | t<-getCannonHeadShot (d,e,f), (containsTuple t ww) || (t==wc), not (null (getCannonHead (d,e,f))), not (containsTuple (head (getCannonHead (d,e,f))) ww), not (containsTuple (head (getCannonHead (d,e,f))) bb)] ++ [(d,t) | t<-getCannonTailShot (d,e,f), (containsTuple t ww) || (t==wc), not (null (getCannonTail (d,e,f))), not (containsTuple (head (getCannonTail (d,e,f))) ww), not (containsTuple (head (getCannonTail (d,e,f))) bb)] ++ listCannonShots ((a,b,c):ws,bs) (ww,bb) (wc,bc) playerIsWhite

cannonShots s = listCannonShots (cannons s) (listSoldiers a) (cities s) b where (a,b) = buildBoard s

allMoves s = (steps s) ++ (backups s) ++ (cannonMoves s) ++ (cannonShots s)

moveToString :: ((Int,Int),(Int,Int)) -> String
moveToString ((a,b),(c,d)) = [chr (97+b)] ++ [chr (57-a)] ++ "-" ++ [chr (97+d)] ++ [chr (57-c)]

allMovesToString :: [((Int,Int),(Int,Int))] -> String
allMovesToString [] = []
allMovesToString (m:ms) = if not (null (allMovesToString ms)) then (moveToString m) ++ "," ++ allMovesToString ms else (moveToString m)