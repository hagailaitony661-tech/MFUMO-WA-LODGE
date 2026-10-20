import streamlit as st
import pandas as pd
from datetime import datetime, date, timedelta

# Set page configuration
st.set_page_config(
    page_title="Mfumo wa Usimamizi wa Lodgi | Lodge Management System",
    page_icon="🏨",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Custom Styling
st.markdown("""
<style>
    .metric-card {
        background-color: #f8f9fa;
        border-radius: 10px;
        padding: 15px;
        border-left: 5px solid #1E88E5;
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
    .status-vacant {
        color: #2E7D32;
        font-weight: bold;
    }
    .status-occupied {
        color: #C62828;
        font-weight: bold;
    }
    .status-cleaning {
        color: #EF6C00;
        font-weight: bold;
    }
</style>
""", unsafe_allow_html=True)

# Initialize Session State Data
if "rooms" not in st.session_state:
    st.session_state.rooms = [
        {"namba": "101", "aina": "Single", "bei": 35000, "hali": "Wazi", "sakafu": "Ghorofa ya 1"},
        {"namba": "102", "aina": "Single", "bei": 35000, "hali": "Kimechukuliwa", "sakafu": "Ghorofa ya 1"},
        {"namba": "103", "aina": "Double", "bei": 60000, "hali": "Wazi", "sakafu": "Ghorofa ya 1"},
        {"namba": "201", "aina": "Double", "bei": 60000, "hali": "Kimechukuliwa", "sakafu": "Ghorofa ya 2"},
        {"namba": "202", "aina": "VIP", "bei": 120000, "hali": "Wazi", "sakafu": "Ghorofa ya 2"},
        {"namba": "301", "aina": "VIP Suite", "bei": 180000, "hali": "Wazi", "sakafu": "Ghorofa ya 3"},
    ]

if "bookings" not in st.session_state:
    leo = str(date.today())
    kesho = str(date.today() + timedelta(days=1))
    st.session_state.bookings = [
        {
            "id": 1,
            "jina": "Juma Bakari",
            "simu": "0754123456",
            "kitambulisho": "19900101-12345-00001",
            "chumba": "102",
            "tarehe_kuingia": leo,
            "tarehe_kutoka": kesho,
            "siku": 1,
            "jumla": 35000,
            "malipo": 35000,
            "njia_ya_malipo": "M-Pesa",
            "hali": "Yupo Ndani",
        },
        {
            "id": 2,
            "jina": "Amina Salum",
            "simu": "0712987654",
            "kitambulisho": "19880520-54321-00002",
            "chumba": "201",
            "tarehe_kuingia": leo,
            "tarehe_kutoka": kesho,
            "siku": 1,
            "jumla": 60000,
            "malipo": 60000,
            "njia_ya_malipo": "Tigo Pesa",
            "hali": "Yupo Ndani",
        }
    ]

if "payments" not in st.session_state:
    leo = str(date.today())
    st.session_state.payments = [
        {"tarehe": leo, "mteja": "Juma Bakari", "chumba": "102", "kiasi": 35000, "njia": "M-Pesa"},
        {"tarehe": leo, "mteja": "Amina Salum", "chumba": "201", "kiasi": 60000, "njia": "Tigo Pesa"}
    ]

# Sidebar Navigation
st.sidebar.image("https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=400&q=80", use_container_width=True)
st.sidebar.title("🏨 LODGE MANAGER")
st.sidebar.markdown("**Mfumo wa Usimamizi wa Lodgi**")
menu = st.sidebar.radio(
    "Menyu Kuu / Menu:",
    ["📊 Dashboard ya Muhtasari", "🛏️ Usimamizi wa Vyumba", "🔑 Usajili (Check-in / Check-out)", "📈 Ripoti na Kumbukumbu"]
)

st.sidebar.divider()
st.sidebar.info("💡 Mfumo huu unakusaidia kufuatilia wateja, vyumba, na mapato ya kila siku kwa urahisi.")

# Helper Functions
def get_room_status(room_no):
    for r in st.session_state.rooms:
        if r["namba"] == room_no:
            return r["hali"]
    return "Haikupatikana"

# 1. DASHBOARD YA MUHTASARI
if menu == "📊 Dashboard ya Muhtasari":
    st.title("📊 Dashboard ya Muhtasari wa Lodgi")
    st.write(f"Tarehe ya Leo: **{datetime.now().strftime('%d/%m/%Y')}**")

    total_rooms = len(st.session_state.rooms)
    occupied_rooms = sum(1 for r in st.session_state.rooms if r["hali"] == "Kimechukuliwa")
    vacant_rooms = sum(1 for r in st.session_state.rooms if r["hali"] == "Wazi")
    cleaning_rooms = sum(1 for r in st.session_state.rooms if r["hali"] == "Usafi")

    # Mapato ya Leo
    today_str = str(date.today())
    today_income = sum(p["kiasi"] for p in st.session_state.payments if p["tarehe"] == today_str)

    # Metrics
    c1, c2, c3, c4 = st.columns(4)
    with c1:
        st.metric(label="Jumla ya Vyumba", value=total_rooms)
    with c2:
        st.metric(label="Vilivyochukuliwa (Occupied)", value=occupied_rooms, delta=f"{occupied_rooms}/{total_rooms}")
    with c3:
        st.metric(label="Vilivyo Wazi (Vacant)", value=vacant_rooms)
    with c4:
        st.metric(label="Mapato ya Leo (TZS)", value=f"{today_income:,.0f}/=")

    st.divider()

    # Visual Layout ya Vyumba
    st.subheader("🗂️ Muhtasari wa Hali ya Vyumba kwa Haraka")
    cols = st.columns(3)
    for idx, room in enumerate(st.session_state.rooms):
        col = cols[idx % 3]
        color = "🟢" if room["hali"] == "Wazi" else ("🔴" if room["hali"] == "Kimechukuliwa" else "🟠")
        with col:
            with st.container(border=True):
                st.markdown(f"### {color} Chumba {room['namba']}")
                st.write(f"**Aina:** {room['aina']} | **Ghorofa:** {room.get('sakafu', 'Chini')}")
                st.write(f"**Bei kwa siku:** TZS {room['bei']:,}")
                st.write(f"**Hali:** **{room['hali']}**")

    # Wageni waliopo ndani sasa
    st.divider()
    st.subheader("👥 Wageni Waliopo Ndani kwa Sasa (Active Guests)")
    active_guests = [b for b in st.session_state.bookings if b["hali"] == "Yupo Ndani"]
    if active_guests:
        df_active = pd.DataFrame(active_guests)[["jina", "simu", "chumba", "tarehe_kuingia", "tarehe_kutoka", "malipo", "njia_ya_malipo"]]
        df_active.columns = ["Jina la Mteja", "Simu", "Chumba", "Kuingia", "Kutoka", "Kiasi Kilicholipwa (TZS)", "Njia ya Malipo"]
        st.dataframe(df_active, use_container_width=True)
    else:
        st.info("Hakuna mteja aliyepo ndani kwa sasa.")

# 2. USIMAMIZI WA VYUMBA
elif menu == "🛏️ Usimamizi wa Vyumba":
    st.title("🛏️ Usimamizi wa Vyumba (Room Management)")

    tab1, tab2 = st.tabs(["📋 Orodha ya Vyumba", "➕ Ongeza Chumba Kipya"])

    with tab1:
        st.subheader("Orodha ya Vyumba Vyote")
        df_rooms = pd.DataFrame(st.session_state.rooms)
        df_rooms.columns = ["Namba ya Chumba", "Aina ya Chumba", "Bei kwa Siku (TZS)", "Hali ya Chumba", "Sakafu / Ghorofa"]
        st.dataframe(df_rooms, use_container_width=True)

        st.divider()
        st.subheader("Badilisha Hali ya Chumba kwa Mkono")
        col_r1, col_r2, col_r3 = st.columns([2, 2, 1])
        with col_r1:
            selected_room = st.selectbox("Chagua Chumba:", [r["namba"] for r in st.session_state.rooms])
        with col_r2:
            new_status = st.selectbox("Hali Mpya:", ["Wazi", "Kimechukuliwa", "Usafi", "Matengenezo"])
        with col_r3:
            st.write("")
            st.write("")
            if st.button("Hifadhi Mabadiliko"):
                for r in st.session_state.rooms:
                    if r["namba"] == selected_room:
                        r["hali"] = new_status
                st.success(f"Chumba {selected_room} kimewekwa kuwa '{new_status}'!")
                st.rerun()

    with tab2:
        st.subheader("Fomu ya Kuongeza Chumba Kipya")
        with st.form("form_add_room"):
            c1, c2 = st.columns(2)
            with c1:
                namba_chumba = st.text_input("Namba ya Chumba (mfano: 104, 205, VIP 1):")
                aina_chumba = st.selectbox("Aina ya Chumba:", ["Single", "Double", "VIP", "VIP Suite", "Executive"])
            with c2:
                bei_kwa_siku = st.number_input("Bei kwa Siku (TZS):", min_value=10000, step=5000, value=40000)
                sakafu = st.selectbox("Sakafu / Ghorofa:", ["Ghorofa ya Chini", "Ghorofa ya 1", "Ghorofa ya 2", "Ghorofa ya 3"])

            submit_room = st.form_submit_button("➕ Hifadhi Chumba Kipya")
            if submit_room:
                if not namba_chumba:
                    st.error("Tafadhali weka namba ya chumba!")
                elif any(r["namba"] == namba_chumba for r in st.session_state.rooms):
                    st.error(f"Chumba chenye namba {namba_chumba} tayari kipo!")
                else:
                    st.session_state.rooms.append({
                        "namba": namba_chumba,
                        "aina": aina_chumba,
                        "bei": bei_kwa_siku,
                        "hali": "Wazi",
                        "sakafu": sakafu
                    })
                    st.success(f"Chumba {namba_chumba} kimeongezwa kikamilifu!")
                    st.rerun()

# 3. USAJILI WA WATEJA (CHECK-IN / CHECK-OUT)
elif menu == "🔑 Usajili (Check-in / Check-out)":
    st.title("🔑 Usajili wa Wateja (Check-in & Check-out)")

    tab_in, tab_out = st.tabs(["📝 Check-In (Sajili Mteja Mpya)", "🚪 Check-Out (Kutoa Mteja)"])

    with tab_in:
        st.subheader("Fomu ya Kusajili Mteja Anayeingia")
        vacant_rooms = [r for r in st.session_state.rooms if r["hali"] == "Wazi"]

        if not vacant_rooms:
            st.warning("⚠️ Samahani, hakuna chumba kilicho wazi kwa sasa!")
        else:
            with st.form("checkin_form"):
                col_a, col_b = st.columns(2)
                with col_a:
                    jina = st.text_input("Jina Kamili la Mteja:*")
                    simu = st.text_input("Namba ya Simu:* (mfano: 07XXXXXXXX)")
                    nida = st.text_input("Namba ya Kitambulisho / NIDA / Pasipoti:*")

                with col_b:
                    room_options = {f"Chumba {r['namba']} - {r['aina']} (TZS {r['bei']:,}/siku)": r for r in vacant_rooms}
                    selected_room_label = st.selectbox("Chagua Chumba Kilicho Wazi:*", list(room_options.keys()))
                    chosen_room = room_options[selected_room_label]

                    col_d1, col_d2 = st.columns(2)
                    with col_d1:
                        tarehe_kuingia = st.date_input("Tarehe ya Kuingia:", value=date.today())
                    with col_d2:
                        tarehe_kutoka = st.date_input("Tarehe ya Kutoka:", value=date.today() + timedelta(days=1))

                # Hesabu ya gharama
                diff_days = (tarehe_kutoka - tarehe_kuingia).days
                if diff_days <= 0:
                    diff_days = 1
                jumla_gharama = diff_days * chosen_room["bei"]

                st.info(f"📅 Idadi ya siku: **{diff_days}** | Jumla ya Gharama: **TZS {jumla_gharama:,.0f}**")

                col_c1, col_c2 = st.columns(2)
                with col_c1:
                    kiasi_kilicholipwa = st.number_input("Kiasi Kilicholipwa (TZS):", min_value=0, max_value=int(jumla_gharama * 2), value=int(jumla_gharama))
                with col_c2:
                    njia_ya_malipo = st.selectbox("Njia ya Malipo:", ["Cash (Pesa Taslimu)", "M-Pesa", "Tigo Pesa", "Airtel Money", "HaloPesa", "Benki / Kadi"])

                submit_checkin = st.form_submit_button("✅ Kamilisha Usajili (Check-in)")
                if submit_checkin:
                    if not jina or not simu or not nida:
                        st.error("Tafadhali jaza taarifa zote muhimu zenye alama ya (*)")
                    else:
                        # 1. Hifadhi Booking
                        booking_id = len(st.session_state.bookings) + 1
                        st.session_state.bookings.append({
                            "id": booking_id,
                            "jina": jina,
                            "simu": simu,
                            "kitambulisho": nida,
                            "chumba": chosen_room["namba"],
                            "tarehe_kuingia": str(tarehe_kuingia),
                            "tarehe_kutoka": str(tarehe_kutoka),
                            "siku": diff_days,
                            "jumla": jumla_gharama,
                            "malipo": kiasi_kilicholipwa,
                            "njia_ya_malipo": njia_ya_malipo,
                            "hali": "Yupo Ndani"
                        })

                        # 2. Badilisha status ya chumba kuwa Kimechukuliwa (Occupied)
                        for r in st.session_state.rooms:
                            if r["namba"] == chosen_room["namba"]:
                                r["hali"] = "Kimechukuliwa"

                        # 3. Rekodi malipo
                        if kiasi_kilicholipwa > 0:
                            st.session_state.payments.append({
                                "tarehe": str(date.today()),
                                "mteja": jina,
                                "chumba": chosen_room["namba"],
                                "kiasi": kiasi_kilicholipwa,
                                "njia": njia_ya_malipo
                            })

                        st.success(f"🎉 Mteja {jina} amesajiliwa kikamilifu katika Chumba {chosen_room['namba']}! Hali ya chumba imebadilishwa kuwa 'Kimechukuliwa'.")
                        st.rerun()

    with tab_out:
        st.subheader("Orodha ya Wageni Waliopo Ndani Tayari kwa Check-Out")
        active_list = [b for b in st.session_state.bookings if b["hali"] == "Yupo Ndani"]

        if not active_list:
            st.info("Hakuna mteja anayeishi ndani kwa sasa.")
        else:
            for b in active_list:
                with st.container(border=True):
                    c1, c2, c3, c4 = st.columns([2, 2, 2, 1.5])
                    with c1:
                        st.markdown(f"**Mteja:** {b['jina']}")
                        st.write(f"📞 {b['simu']} | ID: {b['kitambulisho']}")
                    with c2:
                        st.markdown(f"**Chumba:** #{b['chumba']}")
                        st.write(f"Kuingia: {b['tarehe_kuingia']}")
                        st.write(f"Kutoka: {b['tarehe_kutoka']}")
                    with c3:
                        st.write(f"Gharama: TZS {b['jumla']:,}")
                        st.write(f"Imelipwa: TZS {b['malipo']:,}")
                        baki = b['jumla'] - b['malipo']
                        if baki > 0:
                            st.markdown(f"<span style='color:red; font-weight:bold;'>Baki: TZS {baki:,}</span>", unsafe_allow_html=True)
                        else:
                            st.markdown("<span style='color:green; font-weight:bold;'>Imelipwa Kamili</span>", unsafe_allow_html=True)
                    with c4:
                        st.write("")
                        if st.button("🚪 Check-out", key=f"btn_out_{b['id']}"):
                            # 1. Update booking
                            b["hali"] = "Ametoka (Checked-Out)"
                            # 2. Update room status to Wazi
                            for r in st.session_state.rooms:
                                if r["namba"] == b["chumba"]:
                                    r["hali"] = "Wazi"
                            st.success(f"Mteja {b['jina']} amefanya Check-out! Chumba {b['chumba']} sasa kiko 'Wazi'.")
                            st.rerun()

# 4. RIPOTI NA KUMBUKUMBU
elif menu == "📈 Ripoti na Kumbukumbu":
    st.title("📈 Ripoti na KumbuKumbu (Reports & History)")

    rep_tab1, rep_tab2 = st.tabs(["💵 Historia ya Malipo", "📜 Kumbukumbu ya Wateja Wote"])

    with rep_tab1:
        st.subheader("Historia ya Malipo Yote")
        if st.session_state.payments:
            df_pay = pd.DataFrame(st.session_state.payments)
            df_pay.columns = ["Tarehe", "Jina la Mteja", "Namba ya Chumba", "Kiasi (TZS)", "Njia ya Malipo"]
            st.dataframe(df_pay, use_container_width=True)

            jumla_mapato = sum(p["kiasi"] for p in st.session_state.payments)
            st.markdown(f"### Jumla ya Mapato Yote Yaliyokusanywa: **TZS {jumla_mapato:,.0f}**")
        else:
            st.info("Hakuna rekodi ya malipo bado.")

    with rep_tab2:
        st.subheader("Kumbukumbu ya Wateja Wote Walioingia")
        if st.session_state.bookings:
            df_book = pd.DataFrame(st.session_state.bookings)
            st.dataframe(df_book, use_container_width=True)
        else:
            st.info("Hakuna kumbukumbu za wateja bado.")
