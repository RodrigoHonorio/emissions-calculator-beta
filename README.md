# 🌍 S.P.I.R.E. — Spatial Prediction & Impact Quantification of Reactive Emissions

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL/PostGIS](https://img.shields.io/badge/PostGIS-15--3.3-blue.svg)](https://postgis.net/)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)
[![Licence](https://img.shields.io/badge/Licence-MIT-yellow.svg)](LICENSE)

> **S.P.I.R.E.** is a high-performance open-source backend engine designed to model, quantify, and visualise real-time Volatile Organic Compound (VOC) emissions and evaporative dispersion across Greater London.

---

## 🔬 Scientific Rigour & Peer-Reviewed References

The precision of environmental models is the central pillar of this project. **All mathematical equations, emission factors, and plume dispersion algorithms utilised in S.P.I.R.E. are strictly derived from peer-reviewed scientific literature and official environmental agency methodologies.**

* 📖 **Full Transparency:** Every formula implemented within the codebase contains inline code documentation referencing the corresponding academic publications (DOIs, authors, and papers).
* 📁 **Scientific Repository:** Reference papers, methodological notes, and foundational equations are archived in the [`/docs/papers`](./docs/papers) directory for complete auditability by researchers and atmospheric scientists.

---

## 🤝 Voluntary Project & Community Support

This is a **100% voluntary and open-source project**. We firmly believe that open access to atmospheric data and transparent environmental modelling benefits the entire community.

**All contributions are warmly welcomed!** You do not need to be an atmospheric chemist to participate:

* 💻 **Java / Spring Boot Developers:** Query optimisation, REST API architecture, and external service integrations.
* 🗺️ **GIS & Spatial Data Specialists:** Spatial modelling, PostGIS indexing, and geoprocessing workflows.
* 🧪 **Environmental Scientists & Chemists:** Validation of emission factors, dispersion equations, and methodology refinement.
* 📝 **Technical Writers & Translators:** Documentation enhancement, setup guides, and content refinement.

> Interested in contributing? Open an issue, submit a pull request, or reach out to the maintainers!

---

## 🚀 Key Capabilities

* ⛽ **Spatial Mapping of Sources:** Geofencing and spatial indexing of petrol stations and static emission points using **PostGIS** and **Hibernate Spatial**.
* 🌡️ **Real-Time Meteorological Integration:** Ingestion of ambient temperature, wind velocity, and atmospheric direction via the **Open-Meteo API**.
* 🌫️ **Atmospheric Monitoring:** Integration with the **London Air Quality Network (LAQN)** for background data calibration.
* 📊 **Evaporative Dispersion Simulation:** Dynamic plume calculations based on instantaneous thermodynamic conditions.

---

## 🛠️ Tech Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Java 21 / 17 | Modern, type-safe, and performant backend code |
| **Framework** | Spring Boot 3 | Web REST, Spring Data JPA, Actuator, Validation |
| **Database** | PostgreSQL 15 + PostGIS | Enterprise spatial data storage and GIS queries |
| **Spatial ORM** | Hibernate Spatial / JTS | Mapping spatial geometries (`Point`, `Polygon`, etc.) |
| **Schema Management**| Flyway Migration | Versioned, auditable SQL migrations |
| **Infrastructure** | Docker / Docker Compose | Containerised database environment |

---

## ⚡ Running the Project Locally

### Prerequisites
* **Java 17/21** SDK installed
* **Docker & Docker Compose** running
* **Maven 3.8+** (or use the provided `./mvnw` wrapper)

### 1. Clone the repository
```bash
git clone [https://github.com/RodrigoHonorio/emissions-calculator-beta.git](https://github.com/RodrigoHonorio/emissions-calculator-beta.git)
cd emissions-calculator-beta
