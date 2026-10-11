#!/usr/bin/env python3
"""生成本地音效 wav（木鱼敲击 / 计时铃声），无外部依赖。

输出到 app/src/main/res/raw/{knock,bell}.wav
"""
import math
import random
import struct
import wave
from pathlib import Path

SR = 44100
OUT = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "res" / "raw"


def to_wav(samples, path):
    path.parent.mkdir(parents=True, exist_ok=True)
    with wave.open(str(path), "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(SR)
        w.writeframes(b"".join(
            struct.pack("<h", max(-32767, min(32767, int(s * 32767)))) for s in samples
        ))
    print(f"wrote {path} ({len(samples)/SR:.2f}s)")


def knock():
    """短促木鱼敲击：低频腔体共鸣 + 硬物撞击噪声。"""
    dur = 0.26
    n = int(SR * dur)
    rnd = random.Random(7)
    out = []
    for i in range(n):
        t = i / SR
        # 腔体共鸣（略带失谐的双分音）
        body = (math.sin(2 * math.pi * 340 * t) * 0.7
                + math.sin(2 * math.pi * 523 * t) * 0.3)
        # 撞击瞬态：前 8ms 噪声
        noise = rnd.uniform(-1, 1) * (math.exp(-t * 600) if t < 0.01 else 0.0)
        env = math.exp(-t * 42)
        out.append((body * env + noise * 0.6) * 0.85)
    # 第二下轻击（余音错觉）
    for i in range(int(SR * 0.1)):
        t = i / SR
        body = math.sin(2 * math.pi * 355 * t)
        out[int(SR * 0.075) + i] += body * math.exp(-t * 55) * 0.25
    return out


def bell():
    """电子铃声：非谐分音叠加，两声连响。"""
    dur = 2.2
    n = int(SR * dur)
    out = [0.0] * n
    partials = [(1.0, 1.0), (2.76, 0.55), (5.40, 0.30), (8.93, 0.18)]
    base = 660.0
    for start in (0.0, 0.62):
        off = int(SR * start)
        for i in range(n - off):
            t = i / SR
            env = math.exp(-t * 3.2)
            v = sum(math.sin(2 * math.pi * base * r * t) * a for r, a in partials)
            v += math.sin(2 * math.pi * base * 1.5 * t) * 0.12 * math.exp(-t * 8)
            out[off + i] += v * env * 0.30
    peak = max(abs(x) for x in out)
    return [x / peak * 0.9 for x in out]


if __name__ == "__main__":
    to_wav(knock(), OUT / "knock.wav")
    to_wav(bell(), OUT / "bell.wav")
