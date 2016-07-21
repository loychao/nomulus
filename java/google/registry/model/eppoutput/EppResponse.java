// Copyright 2016 The Domain Registry Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package google.registry.model.eppoutput;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import google.registry.model.Buildable;
import google.registry.model.ImmutableObject;
import google.registry.model.contact.ContactResource;
import google.registry.model.domain.DomainApplication;
import google.registry.model.domain.DomainRenewData;
import google.registry.model.domain.DomainResource;
import google.registry.model.domain.fee06.FeeCheckResponseExtensionV06;
import google.registry.model.domain.fee06.FeeCreateResponseExtensionV06;
import google.registry.model.domain.fee06.FeeDeleteResponseExtensionV06;
import google.registry.model.domain.fee06.FeeInfoResponseExtensionV06;
import google.registry.model.domain.fee06.FeeRenewResponseExtensionV06;
import google.registry.model.domain.fee06.FeeTransferResponseExtensionV06;
import google.registry.model.domain.fee06.FeeUpdateResponseExtensionV06;
import google.registry.model.domain.fee11.FeeCheckResponseExtensionV11;
import google.registry.model.domain.fee11.FeeCreateResponseExtensionV11;
import google.registry.model.domain.fee11.FeeDeleteResponseExtensionV11;
import google.registry.model.domain.fee11.FeeRenewResponseExtensionV11;
import google.registry.model.domain.fee11.FeeTransferResponseExtensionV11;
import google.registry.model.domain.fee11.FeeUpdateResponseExtensionV11;
import google.registry.model.domain.fee12.FeeCheckResponseExtensionV12;
import google.registry.model.domain.fee12.FeeCreateResponseExtensionV12;
import google.registry.model.domain.fee12.FeeDeleteResponseExtensionV12;
import google.registry.model.domain.fee12.FeeRenewResponseExtensionV12;
import google.registry.model.domain.fee12.FeeTransferResponseExtensionV12;
import google.registry.model.domain.fee12.FeeUpdateResponseExtensionV12;
import google.registry.model.domain.launch.LaunchCheckResponseExtension;
import google.registry.model.domain.launch.LaunchCreateResponseExtension;
import google.registry.model.domain.launch.LaunchInfoResponseExtension;
import google.registry.model.domain.rgp.RgpInfoExtension;
import google.registry.model.domain.secdns.SecDnsInfoExtension;
import google.registry.model.eppcommon.Trid;
import google.registry.model.eppoutput.CheckData.ContactCheckData;
import google.registry.model.eppoutput.CheckData.DomainCheckData;
import google.registry.model.eppoutput.CheckData.HostCheckData;
import google.registry.model.eppoutput.CreateData.ContactCreateData;
import google.registry.model.eppoutput.CreateData.DomainCreateData;
import google.registry.model.eppoutput.CreateData.HostCreateData;
import google.registry.model.eppoutput.EppOutput.ResponseOrGreeting;
import google.registry.model.host.HostResource;
import google.registry.model.poll.MessageQueueInfo;
import google.registry.model.poll.PendingActionNotificationResponse.ContactPendingActionNotificationResponse;
import google.registry.model.poll.PendingActionNotificationResponse.DomainPendingActionNotificationResponse;
import google.registry.model.transfer.TransferResponse.ContactTransferResponse;
import google.registry.model.transfer.TransferResponse.DomainTransferResponse;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.joda.time.DateTime;

/**
 * The EppResponse class represents an EPP response message.
 *
 * <p>From the RFC: "An EPP server responds to a client command by returning a response to the
 * client.  EPP commands are atomic, so a command will either succeed completely or fail completely.
 * Success and failure results MUST NOT be mixed."
 *
 * @see "http://tools.ietf.org/html/rfc5730#section-2.6"
 */
@XmlType(propOrder = {"result", "messageQueueInfo", "resData", "extensions", "trid"})
public class EppResponse extends ImmutableObject implements ResponseOrGreeting {

  /** The TRID associated with this response. */
  @XmlElement(name = "trID")
  Trid trid;

  /** The command result. The RFC allows multiple failure results, but we always return one. */
  Result result;

  /**
   * The time the command that created this response was executed.
   *
   * <p>This is for logging purposes only and is not returned to the user.
   */
  @XmlTransient
  DateTime executionTime;

  /**
   * The repository id of a new object if this is a create response, or null.
   *
   * <p>This is for logging purposes only and is not returned to the user.
   */
  @XmlTransient
  String createdRepoId;

  /**
   * Information about messages queued for retrieval. This may appear in response to any EPP message
   * (if messages are queued), but in practice this will only be set in response to a poll request.
   */
  @XmlElement(name = "msgQ")
  MessageQueueInfo messageQueueInfo;

  /** Zero or more response "resData" results. */
  @XmlElementRefs({
      @XmlElementRef(type = ContactResource.class),
      @XmlElementRef(type = DomainApplication.class),
      @XmlElementRef(type = DomainResource.class),
      @XmlElementRef(type = HostResource.class),
      @XmlElementRef(type = ContactCheckData.class),
      @XmlElementRef(type = ContactCreateData.class),
      @XmlElementRef(type = ContactPendingActionNotificationResponse.class),
      @XmlElementRef(type = ContactTransferResponse.class),
      @XmlElementRef(type = DomainCheckData.class),
      @XmlElementRef(type = DomainCreateData.class),
      @XmlElementRef(type = DomainPendingActionNotificationResponse.class),
      @XmlElementRef(type = DomainRenewData.class),
      @XmlElementRef(type = DomainTransferResponse.class),
      @XmlElementRef(type = HostCheckData.class),
      @XmlElementRef(type = HostCreateData.class)})
  @XmlElementWrapper
  ImmutableList<? extends ResponseData> resData;

  /** Zero or more response extensions. */
  @XmlElementRefs({
      @XmlElementRef(type = FeeCheckResponseExtensionV06.class),
      @XmlElementRef(type = FeeInfoResponseExtensionV06.class),
      @XmlElementRef(type = FeeCreateResponseExtensionV06.class),
      @XmlElementRef(type = FeeDeleteResponseExtensionV06.class),
      @XmlElementRef(type = FeeRenewResponseExtensionV06.class),
      @XmlElementRef(type = FeeTransferResponseExtensionV06.class),
      @XmlElementRef(type = FeeUpdateResponseExtensionV06.class),
      @XmlElementRef(type = FeeCheckResponseExtensionV11.class),
      @XmlElementRef(type = FeeCreateResponseExtensionV11.class),
      @XmlElementRef(type = FeeDeleteResponseExtensionV11.class),
      @XmlElementRef(type = FeeRenewResponseExtensionV11.class),
      @XmlElementRef(type = FeeTransferResponseExtensionV11.class),
      @XmlElementRef(type = FeeUpdateResponseExtensionV11.class),
      @XmlElementRef(type = FeeCheckResponseExtensionV12.class),
      @XmlElementRef(type = FeeCreateResponseExtensionV12.class),
      @XmlElementRef(type = FeeDeleteResponseExtensionV12.class),
      @XmlElementRef(type = FeeRenewResponseExtensionV12.class),
      @XmlElementRef(type = FeeTransferResponseExtensionV12.class),
      @XmlElementRef(type = FeeUpdateResponseExtensionV12.class),
      @XmlElementRef(type = LaunchCheckResponseExtension.class),
      @XmlElementRef(type = LaunchCreateResponseExtension.class),
      @XmlElementRef(type = LaunchInfoResponseExtension.class),
      @XmlElementRef(type = RgpInfoExtension.class),
      @XmlElementRef(type = SecDnsInfoExtension.class) })
  @XmlElementWrapper(name = "extension")
  ImmutableList<? extends ResponseExtension> extensions;

  public DateTime getExecutionTime() {
    return executionTime;
  }

  public String getCreatedRepoId() {
    return createdRepoId;
  }

  public ImmutableList<? extends ResponseData> getResponseData() {
    return resData;
  }

  public ImmutableList<? extends ResponseExtension> getExtensions() {
    return extensions;
  }
  
  @Nullable
  public ResponseExtension getFirstExtensionOfType(Class<? extends ResponseExtension> clazz) {
    return FluentIterable.from(extensions).filter(clazz).first().orNull();
  }

  @Nullable
  public ResponseExtension
      getFirstExtensionOfType(ImmutableList<Class<? extends ResponseExtension>> classes) {
    for (Class<? extends ResponseExtension> clazz : classes) {
      ResponseExtension extension = getFirstExtensionOfType(clazz);
      if (extension != null) {
        return extension;
      }
    }
    return null;
  }

  @SafeVarargs
  @Nullable
  public final ResponseExtension
      getFirstExtensionOfType(Class<? extends ResponseExtension>... classes) {
    return getFirstExtensionOfType(ImmutableList.copyOf(classes));
  }
  
  public Result getResult() {
    return result;
  }

  /** Marker interface for types that can go in the {@link #resData} field. */
  public interface ResponseData {}

  /** Marker interface for types that can go in the {@link #extensions} field. */
  public interface ResponseExtension {}

  /** Builder for {@link EppResponse} because it is immutable. */
  public static class Builder extends Buildable.Builder<EppResponse> {
    public Builder setTrid(Trid trid) {
      getInstance().trid = trid;
      return this;
    }

    public Builder setResult(Result result) {
      getInstance().result = result;
      return this;
    }

    public Builder setMessageQueueInfo(MessageQueueInfo messageQueueInfo) {
      getInstance().messageQueueInfo = messageQueueInfo;
      return this;
    }

    public Builder setExecutionTime(DateTime executionTime) {
      getInstance().executionTime = executionTime;
      return this;
    }

    public Builder setCreatedRepoId(String createdRepoId) {
      getInstance().createdRepoId = createdRepoId;
      return this;
    }

    public Builder setResData(@Nullable ImmutableList<? extends ResponseData> resData) {
      getInstance().resData = resData;
      return this;
    }

    public Builder setExtensions(@Nullable ImmutableList<? extends ResponseExtension> extensions) {
      getInstance().extensions = extensions;
      return this;
    }
  }
}