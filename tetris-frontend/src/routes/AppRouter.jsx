import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import TetrisBoard from "../components/TetrisBoard";
import Ranking from "../components/Ranking";
import BattlePass from "../components/BattlePass";

const AppRouter = () => (
  <Router>
    <Routes>
      <Route path="/" element={<TetrisBoard />} />
      <Route path="/ranking" element={<Ranking />} />
      <Route path="/battlepass" element={<BattlePass />} />
    </Routes>
  </Router>
);

export default AppRouter;