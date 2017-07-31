package com.stox.nse.data.instrument;

import com.stox.core.downloader.Downloader;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.nse.NseProperties;

public class InstrumentDownloaderFactory {

	private final NseProperties properties;

	public InstrumentDownloaderFactory(final NseProperties properties) {
		this.properties = properties;
	}

	public Downloader<Instrument, ?> getDownloader(final InstrumentType type) {
		Downloader<Instrument, ?> downloader = null;
		switch (type) {
		case CALL:
			break;
		case CORPORATE_BOND:
			downloader = new CorporateBondDownloader(properties.getCorporateBondsInstrumentDownloadUrl());
			break;
		case DEBT:
			downloader = new DebtDownloader(properties.getDebtInstrumentDownloadUrl());
			break;
		case DEPOSITORY_RECEIPTS:
			downloader = new DepositoryReceiptDownloader(properties.getDepositoryReceiptsInstrumentDownloadUrl());
			break;
		case EQUITY:
			downloader = new EquityDownloader(properties.getEquityInstrumentDownloadUrl());
			break;
		case ETF:
			downloader = new EtfDownloader(properties.getEtfInstrumentDownloadUrl());
			break;
		case FUTURE:
			break;
		case GOVERNMENT_SECURITY:
			downloader = new GsecDownloader(properties.getGsecInstrumentsDownloadUrl());
			break;
		case INDEX:
			break;
		case MUTUAL_FUND:
			downloader = new MutualFundDownloader(properties.getMutualFundsInstrumentDownloadUrl());
			break;
		case MUTUAL_FUND_CE:
			downloader = new MutualFundCloseEndedDownloader(properties.getMutualFundsCloseEndedInstrumentDownloadUrl());
			break;
		case PREFERENCE_SHARES:
			downloader = new PreferenceShareDownloader(properties.getPreferenceSharesInstrumentDownloadUrl());
			break;
		case PUT:
			break;
		case WARRANTS:
			downloader = new WarrantDownloader(properties.getWarrantInstrumentDownloadUrl());
			break;
		default:
			break;
		}
		return downloader;
	}
}
