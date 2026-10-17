package me.whereareiam.socialismus.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.platform.common.PlatformMetrics;
import org.bstats.charts.CustomChart;
import org.jetbrains.annotations.NotNull;

/**
 * Reports to bStats through Velocity's bStats instance, which is created when the charts are registered.
 */
@Singleton
public class VelocityMetrics extends PlatformMetrics {
	private final Injector injector;
	private org.bstats.velocity.Metrics metrics;

	@Inject
	public VelocityMetrics(Injector injector) {
		super(injector);
		this.injector = injector;
	}

	@Override
	public void register() {
		metrics = injector.getInstance(org.bstats.velocity.Metrics.Factory.class).make(
				injector.getInstance(VelocitySocialismus.class), Constants.BStats.VELOCITY_ID
		);

		super.register();
	}

	@Override
	protected void addChart(@NotNull CustomChart chart) {
		metrics.addCustomChart(chart);
	}
}
