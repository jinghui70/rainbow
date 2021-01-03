package rainbow.core.bundle;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import rainbow.core.util.Utils;

@JsonPropertyOrder({ "jar", "repository", "source" })
public class Jar {

	@JsonIgnore
	private String group;

	@JsonIgnore
	private String name;

	@JsonIgnore
	private String version;

	@JsonIgnore
	private boolean dev;

	@JsonInclude(Include.NON_NULL)
	private String repository;

	@JsonInclude(Include.NON_DEFAULT)
	private boolean source;

	public Jar() {
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getJar() {
		return String.format("%s:%s:%s", group, name, version);
	}

	public void setJar(String str) {
		String[] part = Utils.split(str.trim(), ':');
		this.group = part[0];
		this.name = part[1];
		this.version = part[2];
	}

	public boolean isDev() {
		return dev;
	}

	public void setDev(boolean dev) {
		this.dev = dev;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public boolean isSource() {
		return source;
	}

	public void setSource(boolean source) {
		this.source = source;
	}

	public String id() {
		return String.format("%s:%s", group, name);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(group).append(':').append(name).append(':').append(version);
		return sb.toString();
	}

	private StringBuilder fileName(StringBuilder sb) {
		return sb.append(name).append('-').append(version).append(".jar");
	}

	private StringBuilder sourceFileName(StringBuilder sb) {
		return sb.append(name).append('-').append(version).append('-').append("sources.jar");
	}

	public String fileName() {
		return fileName(new StringBuilder()).toString();
	}

	public String sourceFileName() {
		return sourceFileName(new StringBuilder()).toString();
	}

	public String url(String repository) {
		StringBuilder sb = new StringBuilder(repository);
		if (!repository.endsWith("/"))
			sb.append('/');
		sb.append(group.replace('.', '/')).append('/').append(name).append('/').append(version).append('/');
		return fileName(sb).toString();
	}

	public String sourceUrl(String repository) {
		StringBuilder sb = new StringBuilder(repository);
		if (!repository.endsWith("/"))
			sb.append('/');
		sb.append(group.replace('.', '/')).append('/').append(name).append('/').append(version).append('/');
		return sourceFileName(sb).toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Jar other = (Jar) obj;
		return Objects.equals(group, other.group) && Objects.equals(name, other.name)
				&& Objects.equals(version, other.version);
	}

}
