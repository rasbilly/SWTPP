module Main where

import System.IO
import Util
import Data.List
import Control.Monad
import Test.HUnit
import System.Environment
import CannonBot
import Data.Char

main = do
    line <- getArgs
    putStrLn (getMove (head line))