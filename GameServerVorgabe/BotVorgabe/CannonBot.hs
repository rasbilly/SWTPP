--- module (NICHT AENDERN!)
module CannonBot where
--- imports (NICHT AENDERN!)
import Data.Char
import Util

--- external signatures (NICHT AENDERN!)
getMove :: String -> String
listMoves :: String -> String

--- YOUR IMPLEMENTATION STARTS HERE ---

getMove _ = " "
listMoves _ = " "

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

cities s = listCities a where (a,_) = buildBoard s

following3Ascending :: Int -> Int -> Int -> Bool
following3Ascending x y z = if (x + 1) == y && (y + 1) == z then True else False

following3Descending :: Int -> Int -> Int -> Bool
following3Descending x y z = if (x - 1) == y && (y - 1) == z then True else False

-- following3 :: Int -> Int -> Int -> Bool
-- following3 x y z = any (==True) [following3Ordered a b c | a<-[x,y,z], b<-[x,y,z], c<-[x,y,z]]

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

listBackups :: ([(Int,Int)],[(Int,Int)]) -> ([(Int,Int)],[(Int,Int)]) -> Bool -> [((Int,Int),(Int,Int))]
listBackups ([],[]) _ _ = []
listBackups (_,[]) _ _ = []
listBackups ([],_) _ _ = []
listBackups (w:ws,b:bs) (ww,bb) playerIsWhite = if playerIsWhite
                                      then if not (isThreatened w (bb)) then listBackups (ws,b:bs) (ww,bb) playerIsWhite else [(w,t) | t<-listBackupCells w playerIsWhite, not (containsTuple (middle2 w t) ww), not (containsTuple (middle2 w t) (bb)), not (containsTuple t ww), not (containsTuple t bb)] ++ listBackups (ws,b:bs) (ww,bb) playerIsWhite
                                      else if not (isThreatened b (ww)) then listBackups (w:ws,bs) (ww,bb) playerIsWhite else [(b,t) | t<-listBackupCells b playerIsWhite, not (containsTuple (middle2 b t) bb), not (containsTuple (middle2 b t) (ww)), not (containsTuple t ww), not (containsTuple t bb)] ++ listBackups (w:ws,bs) (ww,bb) playerIsWhite

backups s = listBackups soldiersList soldiersList playerIsWhite
  where soldiersList = listSoldiers board
        (board,playerIsWhite) = buildBoard s