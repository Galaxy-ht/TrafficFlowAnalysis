# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Architecture

City Traffic Real-time Monitoring and Analysis Platform (城市交通实时监控平台) — four independently deployable modules:

```
traffic-flink (Real-time Stream Processing)
       |
       v
   [Kafka] → HBase / MySQL / Redis
       |
       v
traffic-analysis (Spring Boot REST API, port 630)
       |
       v
traffic-front (Vue 3 dashboard, port 1024)

GCN-tffc (ML training/prediction — offline)
```

**Data flow:** Traffic data streams into Flink via Kafka → Flink detects violations, stores results in MySQL/HBase/Redis → traffic-analysis serves REST APIs → traffic-front renders interactive maps and dashboards. GCN-tffc trains ML models offline for traffic flow prediction.

## Module Details

### traffic-flink — Real-time Stream Processing

- **Stack:** Java 8, Maven, Apache Flink 1.13.6
- **Entry point:** `com.egon.App` launches 4 parallel monitoring threads:
  - `OverSpeedController` — speeding violations (>10% over limit)
  - `DangerousDriverController` — 3+ speeding events (>20% over) within 2 minutes (uses Flink CEP)
  - `TaoPaiCarController` — cloned plate detection (stateful, ValueState per plate)
  - `AverageSpeedMonitorController` — congestion monitoring (5-min sliding window, 1-min slide)
- **Infra:** Kafka (`topic-car`), MySQL (`traffic_flow_analysis`), Redis, HBase
- **Build:** `mvn clean package`
- **Test:** `mvn test`

### traffic-analysis — Backend REST API

- **Stack:** Java 8, Maven, Spring Boot 2.6.1, MyBatis-Plus 3.5.1
- **Entry point:** `TrafficAnalysisApplication.java`
- **Port:** 630, context path `/api`
- **Package:** `com.egon.trafficanalysis` — controllers, services, mappers, entities (PO/DTO/VO)
- **Infra:** MySQL, Redis (session/cache), HBase (big data), QQ Mail SMTP
- **Build:** `mvn clean package`
- **Run:** `mvn spring-boot:run`
- **Test:** `mvn test`

### traffic-front — GIS Dashboard

- **Stack:** Vue 3.2, Vite 5.2, Element Plus, Pinia, Vue Router 4
- **Maps:** Mars2D (Leaflet-based 3D GIS), ECharts
- **Dev server:** port 1024, proxies `/api` → `http://127.0.0.1:630`
- **Commands:** `npm install` / `npm run dev` / `npm run build`

### GCN-tffc — ML Traffic Prediction

- **Stack:** Python 3.12, PyTorch, PyTorch Lightning
- **Entry point:** `main.py` (argparse CLI)
- **Models:** GCN, GRU, TGCN, TCN, MSTTGCN, TCGCN, NTCGCN — all spatiotemporal graph neural networks
- **Data:** CSV speed matrices + adjacency matrices (LosLoop, Shenzhen, Wenyi)
- **Run:** `python main.py --data losloop --model_name TGCN --settings supervised`

## Infrastructure Dependencies

| Service | Config |
| ------- | ------ |
| MySQL   | `127.0.0.1:3306`, database `traffic_flow_analysis`, user `root`/`root` |
| Redis   | `127.0.0.1:6379` |
| Kafka   | `node1.itcast.cn:9092, node2.itcast.cn:9092, node3.itcast.cn:9092` |
| HBase   | ZooKeeper at `node1,node2,node3:2181` |
| Email   | QQ Mail SMTP (`smtp.qq.com:465`) |

## Conventions

- Java package: `com.egon`
- API responses use `Result` wrapper pattern
- Entity naming: `PO` (persistent), `DTO` (transfer), `VO` (view)
- Frontend uses `@/` alias for `src/` directory
- Flink jobs run as separate threads within a single application
- GCN-tffc models follow a common `nn.Module` interface with `forward()` and argparse-based config