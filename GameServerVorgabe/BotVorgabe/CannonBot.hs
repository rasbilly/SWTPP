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

following3Ordered :: Int -> Int -> Int -> Bool
following3Ordered x y z = if (x + 1) == y && (y + 1) == z then True else False

following3 :: Int -> Int -> Int -> Bool
following3 x y z = any (==True) [following3Ordered a b c | a<-[x,y,z], b<-[x,y,z], c<-[x,y,z]]

allEqual :: Eq a => [a] -> Bool
allEqual s = not (any (/=x) s) where x = head s

same3Tuple :: Eq a => (a,a,a) -> (a,a,a) -> Bool
same3Tuple (a,b,c) (d,e,f) = if (a==d && b==e && c==f) || (a==d && b==f && c==e) || (a==e && b==d && c==f) || (a==e && b==f && c==d) || (a==f && b==d && c==e) || (a==f && b==e && c==d) then True else False

containsTuple :: Eq a => (a,a,a) -> [(a,a,a)] -> Bool
containsTuple x xs = any (same3Tuple x) xs

isCannon :: ((Int,Int),(Int,Int),(Int,Int)) -> Bool
isCannon w = (following3 x1 x2 x3 && allEqual [y1,y2,y3]) || (following3 x1 x2 x3 && following3 y1 y2 y3) || (following3 y1 y2 y3 && allEqual [x1,x2,x3]) where ((x1,y1),(x2,y2),(x3,y3)) = w

allPossibilities :: ([(Int,Int)],[(Int,Int)]) -> ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))])
allPossibilities (a,b) = ([(x,y,z) | x<-a,y<-a,z<-a, x/=y,y/=z,x/=z],[(x,y,z) | x<-b,y<-b,z<-b, x/=y,y/=z,x/=z])

removeDuplicateCannons :: ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))]) -> ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))])
removeDuplicateCannons ([],[]) = ([],[])
removeDuplicateCannons (w:ws,bs) | containsTuple w ws = removeDuplicateCannons (ws,bs)
                                 | otherwise = addListTuples ([w],[]) (removeDuplicateCannons (ws,bs))
removeDuplicateCannons (ws,b:bs) | containsTuple b bs = removeDuplicateCannons (ws,bs)
                                 | otherwise = addListTuples ([],[b]) (removeDuplicateCannons (ws,bs))

listCannons :: ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))]) -> ([((Int,Int),(Int,Int),(Int,Int))],[((Int,Int),(Int,Int),(Int,Int))])
listCannons (ws,bs) = ([a | a<-ws, isCannon a],[a | a<-bs, isCannon a])