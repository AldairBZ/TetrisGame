import React, { useEffect, useState } from "react";
import { getScore } from "../api/tetrisApi";

const ScoreBoard = () => {
  const [score, setScore] = useState(0);

  useEffect(() => {
    getScore().then(data => setScore(data.score || 0));
  }, []);

  return (
    <div style={{ color: "#00ff00" }}>
      <h3>Puntuación: {score}</h3>
    </div>
  );
};

export default ScoreBoard;