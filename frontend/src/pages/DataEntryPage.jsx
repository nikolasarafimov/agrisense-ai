import { useState } from "react";

import {
    apiRequest,
} from "../api";


const initialCropData = {
    name: "",
    type: "",
    plantingDate: "",
};


const initialParcelData = {
    location: "",
    size: "",
    soilType: "",
};


const initialActivityData = {
    description: "",
    date: "",
    type: "",
};


export default function DataEntryPage() {
    const [activeTab, setActiveTab] =
        useState("crop");

    const [cropData, setCropData] =
        useState(initialCropData);

    const [parcelData, setParcelData] =
        useState(initialParcelData);

    const [activityData, setActivityData] =
        useState(initialActivityData);

    const [status, setStatus] =
        useState({
            message: "",
            type: "",
        });

    const [
        lastSavedRecord,
        setLastSavedRecord,
    ] = useState(null);

    const [
        isSubmitting,
        setIsSubmitting,
    ] = useState(false);


    const handleCropChange = (event) => {
        const {
            name,
            value,
        } = event.target;

        setCropData(
            (previousData) => ({
                ...previousData,
                [name]: value,
            }),
        );
    };


    const handleParcelChange = (event) => {
        const {
            name,
            value,
        } = event.target;

        setParcelData(
            (previousData) => ({
                ...previousData,
                [name]: value,
            }),
        );
    };


    const handleActivityChange = (event) => {
        const {
            name,
            value,
        } = event.target;

        setActivityData(
            (previousData) => ({
                ...previousData,
                [name]: value,
            }),
        );
    };


    const saveCrop = async (event) => {
        event.preventDefault();

        const name =
            cropData.name.trim();

        const type =
            cropData.type.trim();

        if (
            !name
            || !type
            || !cropData.plantingDate
        ) {
            setStatus({
                message:
                    "Please fill in all crop fields before saving.",
                type: "error",
            });

            return;
        }

        setIsSubmitting(true);

        setStatus({
            message:
                "Saving crop data...",
            type: "info",
        });

        try {
            const savedCrop =
                await apiRequest(
                    "/api/crops",
                    {
                        method: "POST",
                        body: JSON.stringify({
                            name,
                            type,
                            plantingDate:
                            cropData.plantingDate,
                        }),
                    },
                );

            setLastSavedRecord({
                category: "Crop",
                title: savedCrop.name,
                id: savedCrop.id,
            });

            setCropData(
                initialCropData,
            );

            setStatus({
                message:
                    "Crop data was saved successfully.",
                type: "success",
            });

        } catch (error) {
            setStatus({
                message:
                    error.message
                    || "Could not save crop data.",
                type: "error",
            });

        } finally {
            setIsSubmitting(false);
        }
    };


    const saveParcel = async (event) => {
        event.preventDefault();

        const location =
            parcelData.location.trim();

        const soilType =
            parcelData.soilType.trim();

        const size =
            Number(parcelData.size);

        if (
            !location
            || !parcelData.size
            || !soilType
        ) {
            setStatus({
                message:
                    "Please fill in all parcel fields before saving.",
                type: "error",
            });

            return;
        }

        if (
            !Number.isFinite(size)
            || size <= 0
        ) {
            setStatus({
                message:
                    "Parcel size must be greater than zero.",
                type: "error",
            });

            return;
        }

        setIsSubmitting(true);

        setStatus({
            message:
                "Saving parcel data...",
            type: "info",
        });

        try {
            const savedParcel =
                await apiRequest(
                    "/api/parcels",
                    {
                        method: "POST",
                        body: JSON.stringify({
                            location,
                            size,
                            soilType,
                        }),
                    },
                );

            setLastSavedRecord({
                category: "Parcel",
                title:
                savedParcel.location,
                id: savedParcel.id,
            });

            setParcelData(
                initialParcelData,
            );

            setStatus({
                message:
                    "Parcel data was saved successfully.",
                type: "success",
            });

        } catch (error) {
            setStatus({
                message:
                    error.message
                    || "Could not save parcel data.",
                type: "error",
            });

        } finally {
            setIsSubmitting(false);
        }
    };


    const saveActivity = async (event) => {
        event.preventDefault();

        const description =
            activityData.description.trim();

        const type =
            activityData.type.trim();

        if (
            !description
            || !activityData.date
            || !type
        ) {
            setStatus({
                message:
                    "Please fill in all activity fields before saving.",
                type: "error",
            });

            return;
        }

        setIsSubmitting(true);

        setStatus({
            message:
                "Saving activity data...",
            type: "info",
        });

        try {
            const savedActivity =
                await apiRequest(
                    "/api/activities",
                    {
                        method: "POST",
                        body: JSON.stringify({
                            description,
                            date:
                            activityData.date,
                            type,
                        }),
                    },
                );

            setLastSavedRecord({
                category: "Activity",
                title:
                savedActivity.description,
                id: savedActivity.id,
            });

            setActivityData(
                initialActivityData,
            );

            setStatus({
                message:
                    "Activity data was saved successfully.",
                type: "success",
            });

        } catch (error) {
            setStatus({
                message:
                    error.message
                    || "Could not save activity data.",
                type: "error",
            });

        } finally {
            setIsSubmitting(false);
        }
    };


    const changeTab = (tabName) => {
        if (isSubmitting) {
            return;
        }

        setActiveTab(
            tabName,
        );

        setStatus({
            message: "",
            type: "",
        });

        setLastSavedRecord(
            null,
        );
    };


    const renderStatus = () => {
        if (!status.message) {
            return null;
        }

        return (
            <div
                className={
                    `form-alert ${status.type}`
                }
                role={
                    status.type === "error"
                        ? "alert"
                        : "status"
                }
            >
                {status.message}
            </div>
        );
    };


    return (
        <main className="page-shell">
            <section className="page-header-card">
                <span className="section-label">
                    AgriSense AI Data Entry
                </span>

                <h1>
                    Agricultural Data Entry
                </h1>

                <p>
                    Add and store crop, parcel, and field
                    activity records associated with your
                    account.
                </p>
            </section>


            <section
                className="data-entry-tabs"
                aria-label="Agricultural data categories"
            >
                <button
                    type="button"
                    className={
                        activeTab === "crop"
                            ? "active"
                            : ""
                    }
                    aria-pressed={
                        activeTab === "crop"
                    }
                    disabled={isSubmitting}
                    onClick={() =>
                        changeTab("crop")
                    }
                >
                    Crop
                </button>

                <button
                    type="button"
                    className={
                        activeTab === "parcel"
                            ? "active"
                            : ""
                    }
                    aria-pressed={
                        activeTab === "parcel"
                    }
                    disabled={isSubmitting}
                    onClick={() =>
                        changeTab("parcel")
                    }
                >
                    Parcel
                </button>

                <button
                    type="button"
                    className={
                        activeTab === "activity"
                            ? "active"
                            : ""
                    }
                    aria-pressed={
                        activeTab === "activity"
                    }
                    disabled={isSubmitting}
                    onClick={() =>
                        changeTab("activity")
                    }
                >
                    Activity
                </button>
            </section>


            <section className="crop-form-layout">
                {activeTab === "crop" && (
                    <form
                        className="crop-form-card"
                        onSubmit={saveCrop}
                    >
                        <div className="form-group">
                            <label htmlFor="name">
                                Crop name
                            </label>

                            <input
                                id="name"
                                name="name"
                                type="text"
                                maxLength={100}
                                placeholder="Example: Wheat"
                                value={cropData.name}
                                disabled={isSubmitting}
                                onChange={handleCropChange}
                                required
                            />
                        </div>


                        <div className="form-group">
                            <label htmlFor="type">
                                Crop type
                            </label>

                            <input
                                id="type"
                                name="type"
                                type="text"
                                maxLength={100}
                                placeholder="Example: Grain"
                                value={cropData.type}
                                disabled={isSubmitting}
                                onChange={handleCropChange}
                                required
                            />
                        </div>


                        <div className="form-group">
                            <label htmlFor="plantingDate">
                                Planting date
                            </label>

                            <input
                                id="plantingDate"
                                name="plantingDate"
                                type="date"
                                value={
                                    cropData.plantingDate
                                }
                                disabled={isSubmitting}
                                onChange={handleCropChange}
                                required
                            />
                        </div>


                        <button
                            className="primary-action-button"
                            type="submit"
                            disabled={isSubmitting}
                        >
                            {isSubmitting
                                ? "Saving..."
                                : "Save Crop"}
                        </button>

                        {renderStatus()}
                    </form>
                )}


                {activeTab === "parcel" && (
                    <form
                        className="crop-form-card"
                        onSubmit={saveParcel}
                    >
                        <div className="form-group">
                            <label htmlFor="location">
                                Parcel location
                            </label>

                            <input
                                id="location"
                                name="location"
                                type="text"
                                maxLength={200}
                                placeholder="Example: North Field"
                                value={
                                    parcelData.location
                                }
                                disabled={isSubmitting}
                                onChange={handleParcelChange}
                                required
                            />
                        </div>


                        <div className="form-group">
                            <label htmlFor="size">
                                Parcel size
                            </label>

                            <input
                                id="size"
                                name="size"
                                type="number"
                                min="0"
                                step="any"
                                placeholder="Example: 2.5"
                                value={parcelData.size}
                                disabled={isSubmitting}
                                onChange={handleParcelChange}
                                required
                            />
                        </div>


                        <div className="form-group">
                            <label htmlFor="soilType">
                                Soil type
                            </label>

                            <input
                                id="soilType"
                                name="soilType"
                                type="text"
                                maxLength={100}
                                placeholder="Example: Loamy"
                                value={
                                    parcelData.soilType
                                }
                                disabled={isSubmitting}
                                onChange={handleParcelChange}
                                required
                            />
                        </div>


                        <button
                            className="primary-action-button"
                            type="submit"
                            disabled={isSubmitting}
                        >
                            {isSubmitting
                                ? "Saving..."
                                : "Save Parcel"}
                        </button>

                        {renderStatus()}
                    </form>
                )}


                {activeTab === "activity" && (
                    <form
                        className="crop-form-card"
                        onSubmit={saveActivity}
                    >
                        <div className="form-group">
                            <label htmlFor="description">
                                Activity description
                            </label>

                            <input
                                id="description"
                                name="description"
                                type="text"
                                maxLength={255}
                                placeholder="Example: Irrigation completed"
                                value={
                                    activityData.description
                                }
                                disabled={isSubmitting}
                                onChange={
                                    handleActivityChange
                                }
                                required
                            />
                        </div>


                        <div className="form-group">
                            <label htmlFor="date">
                                Activity date
                            </label>

                            <input
                                id="date"
                                name="date"
                                type="date"
                                value={
                                    activityData.date
                                }
                                disabled={isSubmitting}
                                onChange={
                                    handleActivityChange
                                }
                                required
                            />
                        </div>


                        <div className="form-group">
                            <label htmlFor="activityType">
                                Activity type
                            </label>

                            <input
                                id="activityType"
                                name="type"
                                type="text"
                                maxLength={100}
                                placeholder="Example: Irrigation"
                                value={
                                    activityData.type
                                }
                                disabled={isSubmitting}
                                onChange={
                                    handleActivityChange
                                }
                                required
                            />
                        </div>


                        <button
                            className="primary-action-button"
                            type="submit"
                            disabled={isSubmitting}
                        >
                            {isSubmitting
                                ? "Saving..."
                                : "Save Activity"}
                        </button>

                        {renderStatus()}
                    </form>
                )}


                <aside className="crop-preview-card">
                    <span className="section-label">
                        Current Input
                    </span>


                    {activeTab === "crop" && (
                        <>
                            <h2>
                                Crop Preview
                            </h2>

                            <div className="preview-list">
                                <div>
                                    <span>
                                        Name
                                    </span>

                                    <strong>
                                        {cropData.name
                                            || "Not entered"}
                                    </strong>
                                </div>

                                <div>
                                    <span>
                                        Type
                                    </span>

                                    <strong>
                                        {cropData.type
                                            || "Not entered"}
                                    </strong>
                                </div>

                                <div>
                                    <span>
                                        Planting date
                                    </span>

                                    <strong>
                                        {cropData.plantingDate
                                            || "Not selected"}
                                    </strong>
                                </div>
                            </div>
                        </>
                    )}


                    {activeTab === "parcel" && (
                        <>
                            <h2>
                                Parcel Preview
                            </h2>

                            <div className="preview-list">
                                <div>
                                    <span>
                                        Location
                                    </span>

                                    <strong>
                                        {parcelData.location
                                            || "Not entered"}
                                    </strong>
                                </div>

                                <div>
                                    <span>
                                        Size
                                    </span>

                                    <strong>
                                        {parcelData.size
                                            ? `${parcelData.size} ha`
                                            : "Not entered"}
                                    </strong>
                                </div>

                                <div>
                                    <span>
                                        Soil type
                                    </span>

                                    <strong>
                                        {parcelData.soilType
                                            || "Not entered"}
                                    </strong>
                                </div>
                            </div>
                        </>
                    )}


                    {activeTab === "activity" && (
                        <>
                            <h2>
                                Activity Preview
                            </h2>

                            <div className="preview-list">
                                <div>
                                    <span>
                                        Description
                                    </span>

                                    <strong>
                                        {activityData.description
                                            || "Not entered"}
                                    </strong>
                                </div>

                                <div>
                                    <span>
                                        Date
                                    </span>

                                    <strong>
                                        {activityData.date
                                            || "Not selected"}
                                    </strong>
                                </div>

                                <div>
                                    <span>
                                        Type
                                    </span>

                                    <strong>
                                        {activityData.type
                                            || "Not entered"}
                                    </strong>
                                </div>
                            </div>
                        </>
                    )}


                    {lastSavedRecord && (
                        <div className="saved-crop-box">
                            <span className="section-label">
                                Last Saved Record
                            </span>

                            <p>
                                <strong>
                                    {lastSavedRecord.title}
                                </strong>
                                {" "}was saved as{" "}
                                <strong>
                                    {lastSavedRecord.category}
                                </strong>
                                {" "}with ID{" "}
                                <strong>
                                    {lastSavedRecord.id}
                                </strong>.
                            </p>
                        </div>
                    )}
                </aside>
            </section>
        </main>
    );
}